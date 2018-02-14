import com.ibm.mobile.services.data.IBMData;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

/**
 * Created by verygrey on 10.07.2015.
 */

@IBMDataObjectSpecialization("UserStamp")
public class UserStamp extends IBMDataObject {
    public static final String CLASS_NAME = "UserStamp";

    private static final String USERNAME = "Username";
    private static final String TIME = "Time";
    private static final String TITLE = "Title";

    public String getUsername() {
        return (String) getObject(USERNAME);
    }

    public void setUsername(String username) {
        setObject(USERNAME, (username != null) ? username : "");
    }

    public String getTitle() {
        return (String) getObject(TITLE);
    }

    public void setTitle(String title) {
        setObject(TITLE, (title != null) ? title : "");
    }

    public String getTime() {
        return (String) getObject(TIME);
    }

    public void setTime(String time) {
        setObject(TIME, (time != null) ? time : "");
    }

    @Override
    public String toString() {
        return getUsername();
    }
}
