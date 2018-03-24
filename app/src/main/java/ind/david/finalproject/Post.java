package ind.david.finalproject;

/**
 * Created by mac on 3/18/18.
 */

public class Post
{
    public String date,description,fullname,postimage,profileImage,time,uid;

    public Post() {}
    public Post(String date, String description, String fullname, String postimage, String profileImage, String time, String uid) {
        this.date = date;
        this.description = description;
        this.fullname = fullname;
        this.postimage = postimage;
        this.profileImage = profileImage;
        this.time = time;
        this.uid = uid;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "Post{" +
                "date='" + date + '\'' +
                ", description='" + description + '\'' +
                ", fullname='" + fullname + '\'' +
                ", postimage='" + postimage + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", time='" + time + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }



    /*    public String uid,time,date,postimage,description ,profileimage,fullname;


    public Post(){


    }

    public Post(String uid, String time, String date, String postimage, String description, String profileimage, String fullname) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postimage = postimage;
        this.description = description;
        this.profileimage = profileimage;
        this.fullname = fullname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }*/
}
