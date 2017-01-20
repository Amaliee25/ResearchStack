package org.researchstack.backbone.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Menu;
import android.view.MenuItem;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.PermissionRequestManager;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.model.survey.SurveyItemType;
import org.researchstack.backbone.model.survey.factory.ConsentDocumentFactory;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.onboarding.OnboardingSection;
import org.researchstack.backbone.onboarding.OnboardingSectionType;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.callbacks.ActivityCallback;
import org.researchstack.backbone.ui.step.layout.EmailVerificationStepLayout;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.step.layout.StepPermissionRequest;
import org.researchstack.backbone.R;
import org.researchstack.backbone.utils.StepResultHelper;

import java.util.List;

/**
 * Created by TheMDP on 1/14/17.
 *
 * OnboardingTaskActivity serves as the root task activity during the onboarding process
 * It is not much different than its base class ViewTaskActivity, except
 * that it does not allow the pin code view to be shown
 */

public class OnboardingTaskActivity extends ViewTaskActivity implements ActivityCallback {

    /**
     * Used to maintain the step title from the previous step to show on the next step
     * in the case that the next step does not have a valid step title
     */
    protected String previousStepTitle;

    /**
     * @param context used to create intent
     * @param task any task will be displayed correctly
     * @return launchable intent for OnboardingTaskActivity
     */
    public static Intent newIntent(Context context, Task task) {
        Intent intent = new Intent(context, OnboardingTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    @Override
    public void onDataAuth()
    {
        // Onboarding tasks skip pin code data auth and go right to data ready
        onDataReady();
    }

    @Override
    protected StepLayout getLayoutForStep(Step step)
    {
        StepLayout superStepLayout = super.getLayoutForStep(step);

        // Onboarding Tasks will keep the previous step title if none is available
        String title = task.getTitleForStep(this, step);
        if (title == null) {
            setActionBarTitle(previousStepTitle);
        } else {
            previousStepTitle = title;
        }

        setupCustomStepLayouts(superStepLayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(shouldShowBackButton(step));

        return superStepLayout;
    }

    /**
     * Injects TaskResult information into StepLayouts that need more information
     * @param stepLayout step layout that has just been instantiated
     */
    public void setupCustomStepLayouts(StepLayout stepLayout) {
        // Check here for StepLayouts that need results fed into them
        if (stepLayout instanceof EmailVerificationStepLayout) {
            EmailVerificationStepLayout emailStepLayout = (EmailVerificationStepLayout)stepLayout;
            StepResult passwordResult = StepResultHelper
                    .findStepResult(taskResult, ProfileInfoOption.PASSWORD.getIdentifier());
            if (passwordResult != null) {
                emailStepLayout.setPassword((String)passwordResult.getResult());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Create Onboarding Menu which has an "X" or cancel icon
        getMenuInflater().inflate(R.menu.rsb_onboarding_menu, menu);

        // Use DrawableCompat to change menu item color to white
        // DrawableCompat is necessary since the icon is a Vector Drawable
        Drawable drawable = menu.findItem(R.id.rsb_clear_menu_item).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.rsb_white));
        menu.findItem(R.id.rsb_clear_menu_item).setIcon(drawable);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.rsb_clear_menu_item) {
            showCancelAlert();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Make sure user is 100% wanting to cancel, since their data will be discarded
     */
    protected void showCancelAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.rsb_are_you_sure)
                .setPositiveButton(R.string.rsb_discard_results, (dialog, i) -> discardResultsAndFinish())
                .setNegativeButton(R.string.rsb_cancel, null).create().show();
    }

    /**
     * Clear out all the data that has been saved by this Activity
     * And push user back to the Overview screen, or whatever screen was below this Activity
     */
    protected void discardResultsAndFinish() {
        taskResult.getResults().clear();
        DataProvider.getInstance().signOut(this);
        finish();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermission(String id) {
        if (PermissionRequestManager.getInstance().isNonSystemPermission(id)) {
            PermissionRequestManager.getInstance().onRequestNonSystemPermission(this, id);
        } else {
            requestPermissions(new String[] {id}, PermissionRequestManager.PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionRequestManager.PERMISSION_REQUEST_CODE) {
            updateStepLayoutForPermission();
        }
    }

    protected void updateStepLayoutForPermission() {
        StepLayout stepLayout = (StepLayout) findViewById(R.id.rsb_current_step);
        if(stepLayout instanceof StepPermissionRequest) {
            ((StepPermissionRequest) stepLayout).onUpdateForPermissionResult();
        }
    }

    @Override
    @Deprecated
    public void startConsentTask() {
        // deprecated
    }

    public boolean shouldShowBackButton(Step step) {
        switch (step.getIdentifier()) {
            case OnboardingSection.EMAIL_VERIFICATION_IDENTIFIER:
                return false;
            case OnboardingSection.REGISTRATION_IDENTIFIER:
                return false;
        }
        return true;
    }
}
