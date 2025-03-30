describe('E2E - Sugestões de Atividade', () => {
    const NAME = 'Peddy Paper';
    const INSTITUTION = 'DEMO INSTITUTION';
    const PARTICIPANTS = '3';
    const REGION = 'Lisboa';
    const DESCRIPTION = 'Peddy Paper no Parque das Nações';


    beforeEach(() => {
        cy.deleteAllButArs();
        cy.createDemoEntities();
        //criar as duas sugestões de atividade para o voluntário, colocando-as na base de dados (ficheiro database.js)
        //TODO: cy.createTwoDataBaseActivitySuggestions("Atividade1", "DEMO INSTITUTION", "1", "Atividade2", "DEMO INSTITUTION", "2");
    });

    afterEach(() => {
        cy.deleteAllButArs();
    });

    it('Voluntário cria uma nova sugestão de atividade', () => {
        cy.demoVolunteerLogin();

        // Interceptar sugestões
        cy.intercept('GET', '/activitySuggestions/volunteer/*').as('getSuggestions');
        cy.get('[data-cy="volunteerActivitySuggestions"]').click();
        cy.wait('@getSuggestions');

        // Verificar que há 2 sugestões inicialmente
        cy.get('[data-cy="volunteerSuggestionsTable"] tbody tr')
            //.should('have.length', 2);
            .should('have.length', 0);

        // Criar nova sugestão
        cy.intercept('POST', '/activitySuggestions/institution/*').as('postSuggestion');
        cy.get('[data-cy="newActivitySuggestion"]').click();

        cy.get('[data-cy="nameInput"]').type(NAME);
        cy.get('[data-cy="institutionInput"]').click();
        // Aguarda o dropdown e clica na primeira opção
        cy.get('.v-menu__content .v-list-item')
            .first()
            .click();
        cy.get('[data-cy="regionInput"]').type(REGION);
        cy.get('[data-cy="participantsNumberInput"]').type(PARTICIPANTS);
        cy.get('[data-cy="descriptionInput"]').type(DESCRIPTION);

        cy.get('[data-cy=applicationDeadlineInput]').click();
        cy.wait(1000);
        cy.get('button.datepicker-next:visible').click();
        cy.wait(1000);
        cy.get('button.datepicker-day:visible').contains('20').click();


        cy.get('[data-cy=startingDateInput]').click();
        cy.wait(1000);
        cy.get('button.datepicker-next:visible').click();
        cy.wait(1000);
        cy.get('button.datepicker-next:visible').click();
        cy.wait(1000);
        cy.get('button.datepicker-day:visible').contains('1').click();

        cy.get('[data-cy=endingDateInput]').click();
        cy.wait(1000);
        cy.get('button.datepicker-next:visible').click();
        cy.wait(1000);
        cy.get('button.datepicker-next:visible').click();
        cy.wait(1000);
        cy.get('button.datepicker-day:visible').contains('2').click();
        cy.wait(1000);

        cy.get('[data-cy="saveActivitySuggestion"]').click();
        cy.wait('@postSuggestion');

        // Verificar se há 3 sugestões
        cy.wait(1000);
        cy.get('[data-cy="volunteerActivitySuggestions"] tbody tr')
            //.should('have.length', 3);
            .should('have.length', 1)
            .eq(0)
            .children()
            .should('have.length', 10)
        cy.get('[data-cy="volunteerActivitySuggestions"] tbody tr')
            .eq(0).children().eq(0).should('contain', NAME)
        cy.get('[data-cy="volunteerActivitySuggestions"] tbody tr')
            .eq(0).children().eq(1).should('contain', INSTITUTION)
        cy.get('[data-cy="volunteerActivitySuggestions"] tbody tr')
            .eq(0).children().eq(2).should('contain', DESCRIPTION)
        cy.get('[data-cy="volunteerActivitySuggestions"] tbody tr')
            .eq(0).children().eq(3).should('contain', REGION)
        cy.get('[data-cy="volunteerActivitySuggestions"] tbody tr')
            .eq(0).children().eq(4).should('contain', PARTICIPANTS)
        cy.get('[data-cy="volunteerActivitySuggestions"] tbody tr')
            .eq(0).children().eq(9).should('contain', 'IN_REVIEW');

        cy.logout();
        cy.wait(1000);


        cy.demoMemberLogin();

        // Ir para sugestões
        cy.intercept('GET', '/activitySuggestions/institution/*').as('getSuggestions');
        cy.get('[data-cy="institution"]').click();
        cy.get('[data-cy="activitysuggestions"]').click();
        cy.wait('@getSuggestions');

        // Verificar que a primeira sugestão está em IN_REVIEW
        cy.wait(1000);
        cy.get('[data-cy="activitySuggestionsTable"] tbody tr')
            .should('have.length', 1)
            .eq(0)
            .children()
            .should('have.length', 11)
        cy.get('[data-cy="activitySuggestionsTable"] tbody tr')
            .eq(0).children().eq(9).should('contain', 'IN_REVIEW');

        // Aprovar sugestão
        cy.intercept('PUT', '/activitySuggestions/institution/*/*/approve').as('approveSuggestion');
        cy.get('[data-cy="approveActivitySuggestionButton"]').click();
        cy.wait('@approveSuggestion');

        cy.wait(1000);
        cy.logout();


        // Verificar estado aprovado do lado do voluntário
        cy.demoVolunteerLogin();
        // Interceptar sugestões
        cy.intercept('GET', '/activitySuggestions/volunteer/*').as('getSuggestions');
        cy.get('[data-cy="volunteerActivitySuggestions"]').click();
        cy.wait('@getSuggestions');

        // Verificar se há 3 sugestões
        cy.wait(1000);
        cy.get('[data-cy="volunteerActivitySuggestions"] tbody tr')
            //.should('have.length', 3);
            .should('have.length', 1)
            .eq(0)
            .children()
            .should('have.length', 10)
        cy.get('[data-cy="volunteerActivitySuggestions"] tbody tr')
            .eq(0).children().eq(9).should('contain', 'APPROVED');

        cy.wait(1000);
        cy.logout();
        cy.wait(1000);

        // volta a ser membro para rejeitar a sugestão
        cy.demoMemberLogin();

        // Ir para sugestões
        cy.intercept('GET', '/activitySuggestions/institution/*').as('getSuggestions');
        cy.get('[data-cy="institution"]').click();
        cy.get('[data-cy="activitysuggestions"]').click();
        cy.wait('@getSuggestions');

        // Verificar que a primeira sugestão está em APPROVED
        cy.wait(1000);
        cy.get('[data-cy="activitySuggestionsTable"] tbody tr')
            .should('have.length', 1)
            .eq(0)
            .children()
            .should('have.length', 11)
        cy.get('[data-cy="activitySuggestionsTable"] tbody tr')
            .eq(0).children().eq(9).should('contain', 'APPROVED');

        // Aprovar sugestão
        cy.intercept('PUT', '/activitySuggestions/institution/*/*/reject').as('rejectSuggestion');
        cy.get('[data-cy="rejectActivitySuggestionButton"]').click();
        cy.wait('@rejectSuggestion');

        cy.wait(1000);
        cy.logout();


        // Verificar estado aprovado do lado do voluntário
        cy.demoVolunteerLogin();
        // Interceptar sugestões
        cy.intercept('GET', '/activitySuggestions/volunteer/*').as('getSuggestions');
        cy.get('[data-cy="volunteerActivitySuggestions"]').click();
        cy.wait('@getSuggestions');

        // Verificar se há 3 sugestões
        cy.wait(1000);
        cy.get('[data-cy="volunteerActivitySuggestions"] tbody tr')
            //.should('have.length', 3);
            .should('have.length', 1)
            .eq(0)
            .children()
            .should('have.length', 10)
        cy.get('[data-cy="volunteerActivitySuggestions"] tbody tr')
            .eq(0).children().eq(9).should('contain', 'REJECTED');

        cy.wait(1000);
        cy.logout();
        cy.wait(1000);

    });
});