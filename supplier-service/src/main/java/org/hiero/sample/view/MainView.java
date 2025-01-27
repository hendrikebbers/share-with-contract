package org.hiero.sample.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.hiero.sample.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;

@Route
public class MainView extends VerticalLayout implements Runnable {

    private final TextField balanceText;

    private final TextField missingVerificationCountText;

    private final SupplierService supplierService;

    private final FrontendUpdateService frontendUpdateService;

    @Autowired
    public MainView(final FrontendUpdateService frontendUpdateService, final SupplierService supplierService) {
        this.supplierService = supplierService;
        this.frontendUpdateService = frontendUpdateService;

        balanceText = new TextField("-");
        balanceText.setLabel("Account Balance");
        balanceText.setEnabled(false);

        missingVerificationCountText = new TextField("-");
        missingVerificationCountText.setLabel("Missing Verification Count");
        missingVerificationCountText.setEnabled(false);

        add(balanceText, missingVerificationCountText);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        frontendUpdateService.addTask(this);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        frontendUpdateService.removeTask(this);
    }

    @Override
    public void run() {
        final String balance = supplierService.getAccountBalance().toString();
        final String missingVerificationCount = supplierService.getMissingVerificationCount().toString();
        getUI().ifPresent(ui -> ui.access(() -> {
            balanceText.setValue(balance);
            missingVerificationCountText.setValue(missingVerificationCount);
        }));
    }
}