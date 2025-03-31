<template>
  <v-dialog v-model="dialog" persistent width="1300">
    <v-card>
      <v-card-title>
        <span class="headline">New Institution Profile</span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation @submit.prevent="createInstitutionProfile">
          <v-row>
            <v-col cols="12">
              <v-text-field
                label="*Short description"
                :rules="[(v) => !!v || 'Short description is required']"
                required
                v-model="newInstitutionProfile.shortDescription"
                data-cy="shortDescription"
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
            <div>
              <h2>Selected Assessments</h2>
              <div>
                <v-card class="table">
                  <v-data-table
                    :headers="headers"
                    :search="search"
                    v-model="newInstitutionProfile.selectedAssessments"
                    :items="assessments"
                    disable-pagination
                    show-select
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
          v-if="canSave"
          color="blue-darken-1"
          variant="text"
          @click="createInstitutionProfile"
          data-cy="saveInstitutionProfile"
          type="submit"
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
import InstitutionProfile from '@/models/institutionProfile/InstitutionProfile';
import Assessment from '@/models/assessment/Assessment';
import { ISOtoString } from "../../services/ConvertDateService";

@Component({
  methods: { ISOtoString },
})
export default class InstitutionProfileDialog extends Vue {
  @Model('dialog', { type: Boolean, required: true }) readonly dialog!: boolean;
  @Prop({ type: InstitutionProfile, required: true }) readonly institutionProfile!: InstitutionProfile;
  @Prop({ type: Array, required: true }) readonly assessments!: Assessment[];

  newInstitutionProfile: InstitutionProfile = new InstitutionProfile();
  isFormValid = false;
  cypressCondition: boolean = false;

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

  created() {
    this.newInstitutionProfile = new InstitutionProfile(this.institutionProfile);
    this.newInstitutionProfile.shortDescription = "";
  }

  get canSave(): boolean {
    return (
      this.cypressCondition ||
      (!!this.newInstitutionProfile.shortDescription)
    );
  }

  closeDialog() {
    this.$emit('close-institutionProfile-dialog');
  }

  async createInstitutionProfile() {

    if (!(this.$refs.form as Vue & { validate: () => boolean }).validate())
      return;

    try {
      const result = await RemoteServices.createInstitutionProfile(this.newInstitutionProfile);
      this.$emit('create-institutionProfile', result);
      this.closeDialog();
      
    } catch (error) {
      console.log(error);
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style scoped lang="scss"></style>