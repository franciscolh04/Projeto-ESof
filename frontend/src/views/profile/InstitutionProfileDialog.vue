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
                :rules="shortDescriptionRules"
                required
                v-model="newInstitutionProfile.shortDescription"
                data-cy="shortDescription"
                @input="validateForm"
              ></v-text-field>
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
          v-if="isFormValid"
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

@Component
export default class InstitutionProfileDialog extends Vue {
  @Model('dialog', { type: Boolean, required: true }) readonly dialog!: boolean;
  @Prop({ type: InstitutionProfile, required: true }) readonly institutionProfile!: InstitutionProfile;

  newInstitutionProfile: InstitutionProfile = new InstitutionProfile();
  isFormValid = false;

  shortDescriptionRules = [
    (v: string) => !!v?.trim() || 'Short description is required',
    (v: string) => (v?.trim().length >= 10) || 'Short description must be 10 or more characters'
  ];

  created() {
    this.newInstitutionProfile = new InstitutionProfile(this.institutionProfile);
    this.newInstitutionProfile.shortDescription = "";
  }

  async validateForm() {
    if (this.$refs.form) {
      this.isFormValid = await (this.$refs.form as any).validate();
    }
    return this.isFormValid;
  }

  closeDialog() {
    this.$emit('close-institutionProfile-dialog');
  }

  async createInstitutionProfile() {
    const isValid = await this.validateForm();
    if (!isValid) return;

    try {
      // Ensure we trim the description before sending
      const profileToCreate = {
        ...this.newInstitutionProfile,
        shortDescription: this.newInstitutionProfile.shortDescription.trim()
      };

      const result = await RemoteServices.createInstitutionProfile(profileToCreate);
      this.$emit('create-institutionProfile', result);
      this.closeDialog();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style scoped lang="scss"></style>