<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="activitySuggestions"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="activitySuggestionsTable"
    >
      <template v-slot:item.institutionName="{ item }">
        {{ institutionName() }}
      </template>
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
        </v-card-title>
      </template>
      <template v-slot:item.actions="{ item }">
        <v-chip
          class="mr-2 action-button"
          v-if="item.state === 'IN_REVIEW' || item.state === 'REJECTED'"
          @click="approveActivitySuggestion(item)"
          data-cy="approveActivitySuggestionButton"
        >
          <v-icon left color="green">mdi-thumb-up</v-icon>
        </v-chip>
        <v-chip
          class="mr-2 action-button"
          v-if="item.state === 'IN_REVIEW' || item.state === 'APPROVED'"
          @click="rejectActivitySuggestion(item)"
          data-cy="rejectActivitySuggestionButton"
        >
          <v-icon left color="red">mdi-thumb-down</v-icon>
        </v-chip>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Institution from '@/models/institution/Institution';
import ActivitySuggestion from '@/models/activitySuggestion/ActivitySuggestion';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {},
})
export default class InstitutionActivitySuggestionsView extends Vue {
  activitySuggestions: ActivitySuggestion[] = [];
  institution: Institution = new Institution();
  search: string = '';
  headers: object = [
    {
      text: 'Name',
      value: 'name',
      align: 'left',
      width: '10%',
    },
    {
      text: 'Institution',
      value: 'institutionName',
      align: 'left',
      width: '10%',
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
      width: '5%',
    },
    {
      text: 'End Date',
      value: 'formattedEndingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Application Deadline',
      value: 'formattedApplicationDeadline',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'State',
      value: 'state',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Actions',
      value: 'actions',
      align: 'left',
      sortable: false,
      width: '5%',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      let userId = this.$store.getters.getUser.id;
      this.institution = await RemoteServices.getInstitution(userId);
      if (this.institution.id !== null) {
        this.activitySuggestions =
          await RemoteServices.getActivitySuggestionsByInstitution(
            this.institution.id,
          );
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  institutionName() {
    return this.institution.name;
  }

  async approveActivitySuggestion(activitySuggestion: ActivitySuggestion) {
    if (
      activitySuggestion.id !== null &&
      activitySuggestion.institutionId !== null
    ) {
      try {
        const updatedActivitySuggestion =
          await RemoteServices.approveActivitySuggestion(
            activitySuggestion.id,
            activitySuggestion.institutionId,
          );
        this.activitySuggestions = this.activitySuggestions.map((a) =>
          a.id === updatedActivitySuggestion.id ? updatedActivitySuggestion : a,
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async rejectActivitySuggestion(activitySuggestion: ActivitySuggestion) {
    if (
      activitySuggestion.id !== null &&
      activitySuggestion.institutionId !== null
    ) {
      try {
        const updatedActivitySuggestion =
          await RemoteServices.rejectActivitySuggestion(
            activitySuggestion.id,
            activitySuggestion.institutionId,
          );

        this.activitySuggestions = this.activitySuggestions.map((a) =>
          a.id === updatedActivitySuggestion.id ? updatedActivitySuggestion : a,
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
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
