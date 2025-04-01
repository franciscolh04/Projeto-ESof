<template>
  <v-dialog v-model="dialog" persistent width="1300">
    <v-card>
      <v-card-title>
        <span class="headline">New Volunteer Profile</span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation @submit.prevent="createVolunteerProfile">
          <v-row>
            <v-col cols="12">
              <v-text-field
                  label="*Short bio"
                  :rules="[(v) => !!v || 'Short bio is required']"
                  required
                  v-model="newVolunteerProfile.shortBio"
                  data-cy="shortBio"
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
              <div>
                <h2>Selected Participations</h2>
                <div>
                  <v-card class="table">
                    <v-data-table
                        :headers="headers"
                        :search="search"
                        v-model="newVolunteerProfile.selectedParticipations"
                        :items="participations"
                        disable-pagination
                        show-select
                        :hide-default-footer="true"
                        :mobile-breakpoint="0"
                        data-cy="volunteerParticipationsTable"
                    >
                      <template v-slot:item.activityName="{ item }">
                        {{ activityName(item) }}
                      </template>
                      <template v-slot:item.institutionName="{ item }">
                        {{ institutionName(item) }}
                      </template>
                      <template v-slot:item.rating="{ item }">
                        {{ getMemberRating(item) }}
                      </template>
                      <template v-slot:item.review="{ item }">
                        {{ item.memberReview }}
                      </template>
                      <template v-slot:item.acceptanceDate="{ item }">
                        {{ ISOtoString(item.acceptanceDate) }}
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
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
            color="blue-darken-1"
            variant="text"
            @click="closeDialog"
        >
          Close
        </v-btn>
        <v-btn
            v-if="isValidForSave"
            color="blue-darken-1"
            variant="text"
            @click="createVolunteerProfile"
            type="submit"
            data-cy="saveVolunteerProfile"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import VolunteerProfile from '@/models/volunteerProfile/VolunteerProfile';
import Participation from '@/models/participation/Participation';
import { ISOtoString } from "../../services/ConvertDateService";

@Component({
  methods: { ISOtoString },
})
export default class VolunteerProfileDialog extends Vue {
  @Model('dialog', { type: Boolean, required: true }) readonly dialog!: boolean;
  @Prop({ type: VolunteerProfile, required: true }) readonly volunteerProfile!: VolunteerProfile;
  @Prop({ type: Array, required: true }) readonly participations!: Participation[];
  @Prop({ required: true }) activityName!: (participation: Participation) => string;
  @Prop({ required: true }) institutionName!: (participation: Participation) => string;
  @Prop({ required: true }) getMemberRating!: (participation: Participation) => string;

  newVolunteerProfile: VolunteerProfile = new VolunteerProfile();
  isFormValid = false;
  cypressCondition: boolean = false;

  // really needed?
  shortBioRules = [
    (v: string) => (v?.trim().length >= 10) || 'The short bio must be longer than 10 characters'
  ];

  search: string = '';
  headers: object = [
    {
      text: 'Activity Name',
      value: 'activityName',
      align: 'left',
      width: '20%',
    },
    {
      text: 'Institution',
      value: 'institutionName',
      align: 'left',
      width: '20%',
    },
    {
      text: 'Rating',
      value: 'rating',
      align: 'left',
      width: '20%',
    },
    {
      text: 'Review',
      value: 'review',
      align: 'left',
      width: '20%',
    },
    {
      text: 'Acceptance Date',
      value: 'acceptanceDate',
      align: 'left',
      width: '20%',
    },
  ];

  created() {
    this.newVolunteerProfile = new VolunteerProfile(this.volunteerProfile);
    this.newVolunteerProfile.shortBio = '';
  }

  get isValidForSave(): boolean {
    return (
        this.cypressCondition ||
        (!!this.newVolunteerProfile.shortBio)
    );
  }

  closeDialog() {
    this.$emit('close-volunteerProfile-dialog');
  }

  async createVolunteerProfile() {
    if (!(this.$refs.form as Vue & { validate: () => boolean }).validate())
      return;

    try {
      // Ensure we trim the bio before sending
      const profileToCreate = {
        ...this.newVolunteerProfile,
        shortBio: this.newVolunteerProfile.shortBio.trim(),
      };

      const result = await RemoteServices.createVolunteerProfile(profileToCreate);
      this.$emit('create-volunteerProfile', result);
      this.closeDialog();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}

</script>

<style lang="scss" scoped></style>