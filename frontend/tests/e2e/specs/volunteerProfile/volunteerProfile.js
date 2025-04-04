describe('VolunteerProfile', () => {
    beforeEach(() => {
        cy.deleteAllButArs()
        cy.createDemoEntities();
        cy.createDatabaseInfoForVolunteerProfiles();
    });
    afterEach(() => {
        cy.deleteAllButArs()
    });
    it('create volunteer profile', () => {

        const SHORTBIO = 'This is a short bio';
        const ACTIVITY_NAME = 'A1';
        const INSTITUTION_NAME = 'DEMO INSTITUTION';
        const VOLUNTEER_NAME = 'DEMO-VOLUNTEER';
        const RATING = 5;
        const REVIEW = 'Participação excelente, obrigado!';

        // login as volunteer
        cy.demoVolunteerLogin();

        // go to the create page
        cy.intercept('GET', 'participations/volunteer/3').as('availableParticipations')
        cy.intercept('POST', '/profile/volunteer').as('profileInfo');
        cy.intercept('GET', '/profiles/volunteer').as('profilesList')

        cy.get('[data-cy="profiles"]').click();
        cy.get('[data-cy="volunteer-profile"]').click()
        
        // open dialog and fill form
        cy.get('[data-cy="newVolunteerProfile"]').click();
        
        cy.wait('@availableParticipations');

        cy.get('[data-cy="shortBio"]').type(SHORTBIO);

        cy.get('[data-cy="volunteerParticipationsTable"] tbody tr')
            .first()
            .find('.v-data-table__checkbox')
            .click()

        // save profile
        cy.get('[data-cy="saveVolunteerProfile"]').click();

        cy.wait('@profileInfo');

        // check results
        cy.get('[data-cy="volunteerParticipationsTable"] tbody tr')
            .should('have.length', 1)
            .eq(0)
            .children().should('have.length', 4)

        cy.get('[data-cy="volunteerParticipationsTable"] tbody tr')
            .eq(0).children().eq(0).should('contain', ACTIVITY_NAME)
        cy.get('[data-cy="volunteerParticipationsTable"] tbody tr')
            .eq(0).children().eq(1).should('contain', INSTITUTION_NAME);
        cy.get('[data-cy="volunteerParticipationsTable"] tbody tr')
            .eq(0).children().eq(2).should('contain', RATING);
        cy.get('[data-cy="volunteerParticipationsTable"] tbody tr')
            .eq(0).children().eq(3).should('contain', REVIEW)

        cy.get('.stats-container .items')
            .eq(1)
            .get('.icon-wrapper')
            .should('contain.text', '1');

        cy.logout();

        // as an unauthenticated user, check if the new profile is listed in the list of all volunteer profiles
        cy.get('[data-cy="profiles"]').click();
        cy.get('[data-cy="view-profiles"]').click();

        cy.wait('@profilesList');

        cy.get('[data-cy="volunteerProfilesTable"] tbody tr')
            .should('have.length', 1)
            .eq(0)
            .children()
            .should('have.length', 5)

        cy.get('[data-cy="volunteerProfilesTable"] tbody tr')
            .eq(0).children().eq(0).should('contain', VOLUNTEER_NAME);
        cy.get('[data-cy="volunteerProfilesTable"] tbody tr')
            .eq(0).children().eq(1).should('contain', SHORTBIO);
    });
});
