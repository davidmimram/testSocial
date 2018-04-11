package ind.david.finalproject;

import java.util.HashMap;

/**
 * Created by mac on 3/18/18.
 */

public class Post {
    public String date, description, fullName, postimage, profileImage, time, uid;
    public long numOfLikes;
    public HashMap<String, Boolean> iconStore;



    public Post () {
    }

    public Post (String date, String description, String fullName, String postimage, String profileImage, String time, String uid, long numOfLikes, HashMap<String, Boolean> iconStore) {
        this.date = date;
        this.description = description;
        this.fullName = fullName;
        this.postimage = postimage;
        this.profileImage = profileImage;
        this.time = time;
        this.uid = uid;
        this.numOfLikes = numOfLikes;
        this.iconStore = iconStore;
    }


    public String getDate () {
        return date;
    }

    public void setDate (String date) {
        this.date = date;
    }

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String getFullName () {
        return fullName;
    }

    public void setFullName (String fullName) {
        this.fullName = fullName;
    }

    public String getPostimage () {
        return postimage;
    }

    public void setPostimage (String postimage) {
        this.postimage = postimage;
    }

    public String getProfileImage () {
        return profileImage;
    }

    public void setProfileImage (String profileImage) {
        this.profileImage = profileImage;
    }

    public String getTime () {
        return time;
    }

    public void setTime (String time) {
        this.time = time;
    }

    public String getUid () {
        return uid;
    }

    public void setUid (String uid) {
        this.uid = uid;
    }

    public long getNumOfLikes () {
        return numOfLikes;
    }

    public void setNumOfLikes (long numOfLikes) {
        this.numOfLikes = numOfLikes;
    }

    public HashMap<String, Boolean> getIconStore () {
        return iconStore;
    }

    public void setIconStore (HashMap<String, Boolean> iconStore) {
        this.iconStore = iconStore;
    }


    @Override
    public String toString () {
        return "Post{" +
                "date='" + date + '\'' +
                ", description='" + description + '\'' +
                ", fullName='" + fullName + '\'' +
                ", postimage='" + postimage + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", time='" + time + '\'' +
                ", uid='" + uid + '\'' +
                ", numOfLikes=" + numOfLikes +
                ", iconStore=" + iconStore +
                '}';
    }
}
