package Client.DataClasses;

import java.io.Serializable;

public class Comment implements Serializable {

    String userName;
    String content;

    Comment(String _userName, String _content){
        this.userName = _userName;
        this.content = _content;
    }


    String getUserName(){
        return  this.userName;
    }

    String getContent(){
        return this.content;
    }

}
