<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="activitySuggestions"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
          <v-btn
            color="primary"
            dark
            @click="newActivitySuggestion"
            data-cy="newActivitySuggestion"
            >New Activity Suggestion</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:[`item.institution`]="{ item }">
        <span v-for="institution in institutions" :key="institution.id">
          <span v-if="institution.id === item.institutionId">
            {{ institution.name }}
          </span>
        </span>
      </template>
    </v-data-table>
    <activitySuggestion-dialog
      v-if="currentActivitySuggestion && newActivitySuggestionDialog"
      v-model="newActivitySuggestionDialog"
      :activitySuggestion="currentActivitySuggestion"
      :institutions="institutions"
      v-on:save-activitySuggestion="onSaveActivitySuggestion"
      v-on:close-activitySuggestion-dialog="onCloseActivitySuggestionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import ActivitySuggestion from '@/models/activitySuggestion/ActivitySuggestion';
import ActivitySuggestionDialog from '@/views/volunteer/ActivitySuggestionsDialog.vue';
import RemoteServices from '@/services/RemoteServices';
import Institution from '@/models/institution/Institution';

@Component({
  components: {
    'activitySuggestion-dialog': ActivitySuggestionDialog,
  },
})
export default class VolunteerActivitySuggestionsView extends Vue {
  activitySuggestions: ActivitySuggestion[] = [];
  institutions: Institution[] = [];
  search: string = '';

  currentActivitySuggestion: ActivitySuggestion | null = null;
  newActivitySuggestionDialog: boolean = false;

  headers: object = [
    {
      text: 'Name',
      value: 'name',
      align: 'left',
      width: '15%',
    },
    {
      text: 'Institution',
      value: 'institution',
      align: 'left',
      width: '12%',
    },
    {
      text: 'Description',
      value: 'description',
      align: 'left',
      width: '30%',
    },
    {
      text: 'Region',
      value: 'region',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Participants Limit',
      value: 'participantsNumberLimit',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Start Date',
      value: 'formattedStartingDate',
      align: 'left',
      width: '7%',
    },
    {
      text: 'End Date',
      value: 'formattedEndingDate',
      align: 'left',
      width: '7%',
    },
    {
      text: 'Application Deadline',
      value: 'formattedApplicationDeadline',
      align: 'left',
      width: '7%',
    },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'left',
      width: '7%',
    },
    {
      text: 'State',
      value: 'state',
      align: 'left',
      width: '5%',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      let userId = this.$store.getters.getUser.id;
      this.activitySuggestions =
        await RemoteServices.getActivitySuggestionsByVolunteer(userId);
      this.institutions = await RemoteServices.getInstitutions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  newActivitySuggestion() {
    this.currentActivitySuggestion = new ActivitySuggestion();
    this.newActivitySuggestionDialog = true;
  }

  onCloseActivitySuggestionDialog() {
    this.currentActivitySuggestion = null;
    this.newActivitySuggestionDialog = false;
  }

  onSaveActivitySuggestion(activitySuggestion: ActivitySuggestion) {
    this.activitySuggestions.unshift(activitySuggestion);
    this.newActivitySuggestionDialog = false;
    this.currentActivitySuggestion = null;
  }
}
</script>

<style lang="scss" scoped>
.date-fields-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.date-fields-row {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}
</style>
