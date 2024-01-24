package edu.ncsu.csc.itrust2.controllers.api;

import edu.ncsu.csc.itrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.itrust2.models.OfficeVisit;
import edu.ncsu.csc.itrust2.models.User;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.services.OfficeVisitMutationService;
import edu.ncsu.csc.itrust2.services.OfficeVisitService;
import edu.ncsu.csc.itrust2.services.UserService;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SuppressWarnings({"unchecked", "rawtypes"})
public class APIOfficeVisitController extends APIController {

    private final OfficeVisitService officeVisitService;

    private final OfficeVisitMutationService officeVisitMutationService;

    private final UserService userService;

    private final LoggerUtil loggerUtil;

    /**
     * Retrieves a list of all OfficeVisits in the database
     *
     * @return list of office visits
     */
    @GetMapping("/officevisits")
    @PreAuthorize("hasRole('ROLE_HCP')")
    public List<OfficeVisit> getOfficeVisits() {
        loggerUtil.log(TransactionType.VIEW_ALL_OFFICE_VISITS, loggerUtil.getCurrentUsername());
        loggerUtil.log(TransactionType.OPHTHALMOLOGY_VIEW_SURGERY, loggerUtil.getCurrentUsername());
        return (List<OfficeVisit>) officeVisitService.findAll();
    }

    /**
     * Retrieves all of the office visits for the current HCP.
     *
     * @return all of the office visits for the current HCP.
     */
    @GetMapping("/officevisits/HCP")
    @PreAuthorize("hasRole('ROLE_HCP')")
    public List<OfficeVisit> getOfficeVisitsForHCP() {
        final User self = userService.findByName(loggerUtil.getCurrentUsername());
        loggerUtil.log(TransactionType.VIEW_ALL_OFFICE_VISITS, self);
        return officeVisitService.findByHcp(self);
    }

    /**
     * Retrieves a list of all OfficeVisits in the database for the current patient
     *
     * @return list of office visits
     */
    @GetMapping("/officevisits/myofficevisits")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public List<OfficeVisit> getMyOfficeVisits() {
        final User self = userService.findByName(loggerUtil.getCurrentUsername());
        loggerUtil.log(TransactionType.VIEW_ALL_OFFICE_VISITS, self);
        loggerUtil.log(TransactionType.PATIENT_VIEW_SURGERY, self);
        return officeVisitService.findByPatient(self);
    }

    /**
     * Retrieves a list of all OfficeVisits in the database
     *
     * @param id ID of the office visit to retrieve
     * @return list of office visits
     */
    @GetMapping("/officevisits/{id}")
    @PreAuthorize("hasRole('ROLE_HCP')")
    public ResponseEntity getOfficeVisit(@PathVariable final Long id) {
        final User self = userService.findByName(loggerUtil.getCurrentUsername());
        loggerUtil.log(TransactionType.GENERAL_CHECKUP_HCP_VIEW, self);
        if (!officeVisitService.existsById(id)) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(officeVisitService.findById(id), HttpStatus.OK);
    }

    /**
     * Creates and saves a new OfficeVisit from the RequestBody provided.
     *
     * @param visitForm The office visit to be validated and saved
     * @return response
     */
    @PostMapping("/officevisits")
    @PreAuthorize("hasRole('ROLE_HCP')")
    public ResponseEntity createOfficeVisit(@RequestBody final OfficeVisitForm visitForm) {
        final OfficeVisit visit = officeVisitMutationService.create(visitForm);

        loggerUtil.log(
                TransactionType.GENERAL_CHECKUP_CREATE,
                loggerUtil.getCurrentUsername(),
                visit.getPatient().getUsername());
        return new ResponseEntity(visit, HttpStatus.OK);
    }

    /**
     * Creates and saves a new Office Visit from the RequestBody provided.
     *
     * @param id ID of the office visit to update
     * @param visitForm The office visit to be validated and saved
     * @return response
     */
    @PutMapping("/officevisits/{id}")
    @PreAuthorize("hasRole('ROLE_HCP')")
    public ResponseEntity updateOfficeVisit(
            // TODO: what is this never used `id`?
            @PathVariable final Long id, @RequestBody final OfficeVisitForm visitForm) {
        final OfficeVisit visit = officeVisitMutationService.update(visitForm);

        loggerUtil.log(
                TransactionType.GENERAL_CHECKUP_EDIT,
                loggerUtil.getCurrentUsername(),
                visit.getPatient().getUsername());
        return new ResponseEntity(visit, HttpStatus.OK);
    }
}
