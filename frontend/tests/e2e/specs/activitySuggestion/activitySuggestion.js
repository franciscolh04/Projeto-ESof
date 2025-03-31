describe('E2E - Activity Suggestions', () => {
    const NAME = 'Peddy Paper';
    const INSTITUTION = 'DEMO INSTITUTION';
    const PARTICIPANTS = '3';
    const REGION = 'Lisbon';
    const DESCRIPTION = 'Peddy Paper in Lisbon';


    beforeEach(() => {
        cy.deleteAllButArs();
        cy.createDemoEntities();
        cy.createTwoDataBaseActivitySuggestions();
    });

    afterEach(() => {
        cy.deleteAllButArs();
    });

    it('volunteer creates a new activiy suggestion and member approves/rejects it', () => {
        cy.demoVolunteerLogin();

        // Intercept activity suggestions
        cy.intercept('GET', '/activitySuggestions/volunteer/*').as('getSuggestions');
        cy.get('[data-cy="volunteerActivitySuggestions"]').click();
        cy.wait('@getSuggestions');

        // Verify there's two activity suggestions in the beggining
        cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
            .should('have.length', 2);

        // Create another activity suggestion
        cy.intercept('POST', '/activitySuggestions/institution/*').as('postSuggestion');
        cy.get('[data-cy="newActivitySuggestionButton"]').click();

        cy.get('[data-cy="nameInput"]').type(NAME);
        cy.get('[data-cy="institutionInput"]').click();
        // Waits for dropdown and clicks on the first option
        cy.get('.v-menu__content .v-list-item')
            .first()
            .click();
        cy.get('[data-cy="regionInput"]').type(REGION);
        cy.get('[data-cy="participantsNumberInput"]').type(PARTICIPANTS);
        cy.get('[data-cy="descriptionInput"]').type(DESCRIPTION);

        // Selects dates, waits for the interface to complete the steps
        cy.get('[data-cy=applicationDeadlineInput]').click();
        cy.wait(500);
        // go to next month
        cy.get('button.datepicker-next:visible').click();
        cy.wait(500);
        // selects day 25
        cy.get('button.datepicker-day:visible').contains('25').click();


        cy.get('[data-cy=startingDateInput]').click();
        cy.wait(500);
        // go to next month
        cy.get('button.datepicker-next:visible').click();
        cy.wait(500);
        // selects day 26
        cy.get('button.datepicker-day:visible').contains('26').click();

        cy.get('[data-cy=endingDateInput]').click();
        cy.wait(500);
        // go to next month
        cy.get('button.datepicker-next:visible').click();
        cy.wait(500);
        // selects day 27
        cy.get('button.datepicker-day:visible').contains('27').click();

        cy.get('[data-cy="saveActivitySuggestionButton"]').click();
        cy.wait('@postSuggestion');

        // Verify if there is three activity suggestions and if the third one was correctly saved
        cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
            .should('have.length', 3)
            .eq(0)
            .children()
            .should('have.length', 10)
        cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
            .eq(0).children().eq(0).should('contain', NAME)
        cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
            .eq(0).children().eq(1).should('contain', INSTITUTION)
        cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
            .eq(0).children().eq(2).should('contain', DESCRIPTION)
        cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
            .eq(0).children().eq(3).should('contain', REGION)
        cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
            .eq(0).children().eq(4).should('contain', PARTICIPANTS)
        cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
            .eq(0).children().eq(9).should('contain', 'IN_REVIEW');

        cy.logout();
        cy.wait(500);

        // Login as member
        cy.demoMemberLogin();

        // Go to activity suggestions
        cy.intercept('GET', '/activitySuggestions/institution/*').as('getSuggestions');
        cy.get('[data-cy="institution"]').click();
        cy.get('[data-cy="activitysuggestions"]').click();
        cy.wait('@getSuggestions');

        // Verify that the new activity suggestion is IN_REVIEW
        cy.get('[data-cy="institutionActivitySuggestionsTable"] tbody tr')
            .should('have.length', 3)
            .contains(NAME)
            .parent()
            .children()
            .should('have.length', 11)
        cy.get('[data-cy="institutionActivitySuggestionsTable"] tbody tr')
            .eq(0).children().eq(9).should('contain', 'IN_REVIEW');

        // Approve new activity suggestion
        cy.intercept('PUT', '/activitySuggestions/institution/*/*/approve').as('approveSuggestion');
        cy.get('[data-cy="institutionActivitySuggestionsTable"] tbody tr')
            .contains(NAME)
            .parent()
            .find('[data-cy="approveActivitySuggestionButton"]')
            .click();
        cy.wait('@approveSuggestion');

        cy.logout();
        cy.wait(500);


        // Verify if the state of the activity suggestion changed to approved in the volunteer side
        cy.demoVolunteerLogin();
        // Intercept activity suggestions
        cy.intercept('GET', '/activitySuggestions/volunteer/*').as('getSuggestions');
        cy.get('[data-cy="volunteerActivitySuggestions"]').click();
        cy.wait('@getSuggestions');

        //  Verifies if the activity suggestion is approved
        cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
            .contains(NAME)
            .parent()
            .children().eq(9).should('contain', 'APPROVED');
        cy.wait(500);

        cy.logout();
        cy.wait(500);

        // Log in as member again to reject the activity suggesiton
        cy.demoMemberLogin();

        // Go to activity suggestions
        cy.intercept('GET', '/activitySuggestions/institution/*').as('getSuggestions');
        cy.get('[data-cy="institution"]').click();
        cy.get('[data-cy="activitysuggestions"]').click();
        cy.wait('@getSuggestions');

        // Verify if the activity suggestion is approved
        cy.get('[data-cy="institutionActivitySuggestionsTable"] tbody tr')
            .contains(NAME)
            .parent()
            .children().eq(9).should('contain', 'APPROVED');

        // Reject activity suggestion
        cy.intercept('PUT', '/activitySuggestions/institution/*/*/reject').as('rejectSuggestion');
        cy.get('[data-cy="institutionActivitySuggestionsTable"] tbody tr')
            .contains(NAME)
            .parent()
            .find('[data-cy="rejectActivitySuggestionButton"]')
            .click();
        cy.wait('@rejectSuggestion');

        cy.logout();
        cy.wait(500);


        // Verify if the activity suggestion is rejected in the volunteer side
        cy.demoVolunteerLogin();
        // Intercept activity suggestions
        cy.intercept('GET', '/activitySuggestions/volunteer/*').as('getSuggestions');
        cy.get('[data-cy="volunteerActivitySuggestions"]').click();
        cy.wait('@getSuggestions');
        
        // Verifies if the activity suggestion is rejected
        cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
            .contains(NAME)
            .parent()
            .children().eq(9).should('contain', 'REJECTED');

        cy.wait(500);
        cy.logout();

    });
});