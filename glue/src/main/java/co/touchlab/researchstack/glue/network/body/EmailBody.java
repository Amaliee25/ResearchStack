package co.touchlab.researchstack.glue.network.body;
public class EmailBody
{

    /**
     * The identifier for the study under which the user is signing in
     */
    private String study;

    /**
     * The email address of the user's account
     */
    private String email;

    private EmailBody(String study, String email)
    {
        this.study = study;
        this.email = email;
    }
}
