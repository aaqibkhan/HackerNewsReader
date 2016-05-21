package aaqib.taskaaqib.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Used to store network call response
 */
public class ApiResponse implements Parcelable {

    int code;
    boolean success;
    String response;
    String url;
    String error;

    public ApiResponse() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeByte(success ? (byte) 1 : (byte) 0);
        dest.writeString(this.response);
        dest.writeString(this.url);
        dest.writeString(this.error);
    }

    protected ApiResponse(Parcel in) {
        this.code = in.readInt();
        this.success = in.readByte() != 0;
        this.response = in.readString();
        this.url = in.readString();
        this.error = in.readString();
    }

    public static final Creator<ApiResponse> CREATOR = new Creator<ApiResponse>() {
        @Override
        public ApiResponse createFromParcel(Parcel source) {
            return new ApiResponse(source);
        }

        @Override
        public ApiResponse[] newArray(int size) {
            return new ApiResponse[size];
        }
    };
}
