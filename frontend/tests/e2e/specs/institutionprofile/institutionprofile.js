describe('InstitutionProfile', () => {
    beforeEach(() => {
      cy.deleteAllButArs()
      cy.createDatabaseInfoForInstitutionProfile();
    });
  
    afterEach(() => {
      cy.deleteAllButArs()
    });
  
    it('create institution profile', () => {

      const SHORTDESCRIPTION = "Short description";
      const NAME='DEMO-VOLUNTEER'
      const REVIEW='Muito bom!'

      // login as member
      cy.demoMemberLogin();

      // go to the create page 
      cy.intercept('GET', 'profiles/institution/1').as('profile');
      cy.get('[data-cy="profiles"]').click();
      cy.get('[data-cy="member-profile"]').click();
      cy.wait('@profile');

      // open dialog
      cy.intercept('GET', '/institutions/1/assessments').as('availableAccessments')

      cy.get('[data-cy="newInstitutionProfile"]').click();
      cy.wait('@availableAccessments');

      // fill form
      cy.intercept('POST', '/profile/institution').as('profileInfo');

      cy.get('[data-cy="shortDescription"]').type(SHORTDESCRIPTION);

      cy.get('[data-cy="institutionAssessmentsTable"] tbody tr')
      .first()
      .find('.v-data-table__checkbox')
      .click()

      // save profile
      cy.get('[data-cy="saveInstitutionProfile"]').click();

      cy.wait('@profileInfo');

      // check results
      cy.get('[data-cy="institutionAssessmentsTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .children()
      .should('have.length', 3)
      cy.get('[data-cy="institutionAssessmentsTable"] tbody tr')
        .eq(0).children().eq(0).should('contain', NAME)
      cy.get('[data-cy="institutionAssessmentsTable"] tbody tr')
        .eq(0).children().eq(1).should('contain', REVIEW)

      cy.get('.stats-container .items')
      .eq(1)
      .get('.icon-wrapper')
      .should('contain.text', '1');
      
      cy.logout();
  
    });
  });