package co.touchlab.touchkit.rk.common.step;
import co.touchlab.touchkit.rk.common.model.ConsentDocument;
import co.touchlab.touchkit.rk.common.model.ConsentSignature;
import co.touchlab.touchkit.rk.ui.scene.ConsentReviewScene;

public class ConsentReviewStep extends Step
{
    private ConsentSignature signature;
    private ConsentDocument document;
    private final String reasonForConsent;

    public ConsentReviewStep(String identifier, ConsentSignature signature, ConsentDocument document, String reasonForConsent)
    {
        super(identifier);
        this.signature = signature;
        this.document = document;
        this.reasonForConsent = reasonForConsent;
    }

    @Override
    public boolean isShowsProgress()
    {
        return false;
    }

    public ConsentDocument getDocument()
    {
        return document;
    }

    public ConsentSignature getSignature()
    {
        return signature;
    }

    public String getReasonForConsent()
    {
        return reasonForConsent;
    }

    @Override
    public Class getSceneClass()
    {
        return ConsentReviewScene.class;
    }

}
