package com.guoyi.circle.been;

/**
 * Created by Credit on 2017/2/28.
 */

public class UserBean extends ReturnMsg {

    /**
     * Id : 1
     * Name : 1
     * Mobile : 1
     * Sex : false
     * Age : 0
     * Birth : 2017-02-28 13:36:15
     * Address : 广州市天河区
     * Pwd : 123456
     * Pic : /static/img/default.jpg
     */

    private int Id;
    private String Name;
    private String Mobile;
    private boolean Sex;
    private int Age;
    private String Birth;
    private String Address;
    private String Pwd;
    private String Pic;

    public UserBean() {
    }

    public UserBean(int id, String name, String mobile, boolean sex, int age, String birth, String address, String pwd, String pic) {
        Id = id;
        Name = name;
        Mobile = mobile;
        Sex = sex;
        Age = age;
        Birth = birth;
        Address = address;
        Pwd = pwd;
        Pic = pic;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String Mobile) {
        this.Mobile = Mobile;
    }

    public boolean isSex() {
        return Sex;
    }

    public void setSex(boolean Sex) {
        this.Sex = Sex;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int Age) {
        this.Age = Age;
    }

    public String getBirth() {
        return Birth;
    }

    public void setBirth(String Birth) {
        this.Birth = Birth;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getPwd() {
        return Pwd;
    }

    public void setPwd(String Pwd) {
        this.Pwd = Pwd;
    }

    public String getPic() {
        return Pic;
    }

    public void setPic(String Pic) {
        this.Pic = Pic;
    }

    @Override
    public String toString() {
        return "User{" +
                "Id=" + Id +
                ", Name='" + Name + '\'' +
                ", Mobile='" + Mobile + '\'' +
                ", Sex=" + Sex +
                ", Age=" + Age +
                ", Birth='" + Birth + '\'' +
                ", Address='" + Address + '\'' +
                ", Pwd='" + Pwd + '\'' +
                ", Pic='" + Pic + '\'' +
                '}';
    }


}
