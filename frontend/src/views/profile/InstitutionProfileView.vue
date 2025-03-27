<template>
  <div class="container">
    <!-- TODO: Add creation button here (only if there is no profile) -->
    <div v-if="!hasInstitutionProfile">
      <h1>Institution Profile</h1>
      <div class="text-description">
        <p> No institution profile found. Click the button below to create a new one! </p>
      </div>
      <v-btn color="primary" @click="newInstitutionProfile" data-cy="newInstitutionProfile">
        Create Institution Profile
      </v-btn>
    </div>
    <institutionProfile-dialog 
        v-if="!hasInstitutionProfile && institutionProfileDialog"
        v-model="institutionProfileDialog"
        :institutionProfile="institutionProfile"
        :assessments="assessments"
        v-on:create-institutionProfile="onSaveInstitutionProfile"
        v-on:close-institutionProfile-dialog="onCloseInstitutionProfileDialog"
    />
    <div v-if="hasInstitutionProfile">
      <h1>Institution: {{ institutionProfile?.institution?.name || 'N/A' }}</h1>
      <div class="text-description">
        <p><strong>Short Description: </strong> {{ institutionProfile?.shortDescription || 'N/A' }}</p>
      </div>
      <div class="stats-container">
        <div class="items" v-for="(stat, index) in stats" :key="index">
          <div class="icon-wrapper">
            <span>{{ stat.value % 1 === 0 ? stat.value : Number(stat.value).toFixed(2) }}</span>
          </div>
          <div class="project-name">
            <p>{{ stat.label }}</p>
          </div>
        </div>
      </div>

      <div>
        <h2>Selected Assessments</h2>
        <div>
          <v-card class="table">
            <v-data-table
              :headers="headers"
              :search="search"
              :items="institutionProfile?.selectedAssessments"
              disable-pagination
              :hide-default-footer="true"
              :mobile-breakpoint="0"
              data-cy="institutionAssessmentsTable"
            >
              <template v-slot:item.reviewDate="{ item }">
                {{ ISOtoString(item.reviewDate) }}
              </template>
              <template v-slot:top>
                <v-card-title>
                  <v-text-field
                    v-model="search"
                    append-icon="search"
                    label="Search"
                    class="mx-2"
                  />
                  <v-spacer />
                </v-card-title>
              </template>
            </v-data-table>
          </v-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { ISOtoString } from "../../services/ConvertDateService";
import InstitutionProfile from '@/models/institutionProfile/InstitutionProfile';
import InstitutionProfileDialog from '@/views/profile/InstitutionProfileDialog.vue';
import RemoteServices from '@/services/RemoteServices';
import Assessment from '@/models/assessment/Assessment';

@Component({
  components: {
    'institutionProfile-dialog': InstitutionProfileDialog,
  },
  methods: { ISOtoString },
})
export default class InstitutionProfileView extends Vue {
  institutionId: number = 0;
  institutionProfile: InstitutionProfile | null = null;
  assessments: Assessment[] = [];

  institutionProfileDialog: boolean = false;

  search: string = '';
  headers: object = [
    {
      text: 'Volunteer Name',
      value: 'volunteerName',
      align: 'left',
      width: '30%',
    },
    {
      text: 'Review',
      value: 'review',
      align: 'left',
      width: '30%',
    },
    {
      text: 'Review Date',
      value: 'reviewDate',
      align: 'left',
      width: '40%',
    }
  ];

  get stats() {
    return [
      { label: 'Total Members', value: this.institutionProfile?.numMembers || 0 },
      { label: 'Total Assessments', value: this.institutionProfile?.numAssessments || 0 },
      { label: 'Total Activities', value: this.institutionProfile?.numActivities || 0 },
      { label: 'Total Volunteers', value: this.institutionProfile?.numVolunteers || 0 },
      { label: 'Average Rating', value: this.institutionProfile?.averageRating || 0 }
    ];
  }

  get hasInstitutionProfile(): boolean {
    return this.institutionProfile != null && this.institutionProfile.id !== null;
  }

  async created() {
    let institution: InstitutionProfile;

    this.institutionProfile = this.$store.getters.getInstitutionProfile;

    await this.$store.dispatch('loading');
    try {
      this.institutionId = Number(this.$route.params.id);

      if (!this.hasInstitutionProfile) {
        institution = await RemoteServices.getInstitutionProfile(this.institutionId);
        this.institutionProfile = institution?.id ? institution : null;
        if(this.institutionProfile == null){
          this.assessments = await RemoteServices.getInstitutionAssessments(this.institutionId);
        }
      }

    } catch (error) {
      await this.$store.dispatch('error', error);
      this.institutionProfile = null;
    }
    await this.$store.dispatch('clearLoading');
  }

  newInstitutionProfile(){
    this.institutionProfile = new InstitutionProfile();
    this.institutionProfileDialog = true;
  }

  onCloseInstitutionProfileDialog(){
    this.institutionProfile = null;
    this.institutionProfileDialog = false;
  }

  async onSaveInstitutionProfile(institutionProfile: InstitutionProfile){
    await this.$store.dispatch('loading');
    try {
      this.institutionProfile = institutionProfile;
      this.institutionProfileDialog = false;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

}
</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #696969;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}

.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }

  & .icon-wrapper i {
    transform: translateY(5px);
  }
}

.text-description {
  display: block;
  padding: 1em;
}
</style>