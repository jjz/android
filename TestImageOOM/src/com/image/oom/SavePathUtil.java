package com.image.oom;

public class SavePathUtil {

	private String SAVE_ME_PATH;
	private String SAVE_FRIEND_PATH;
	private final String APP_PATH = "getcai";
	private final String IMAGE_PATH = APP_PATH + "/image/";
	private final String FRIEND_IMAGE_PATH = APP_PATH + "/friend/me";

	private SavePathUtil() {
		this.SAVE_ME_PATH = FileUtil.getImageDir("/" + IMAGE_PATH).toString()
				+ "/";
		this.SAVE_FRIEND_PATH = FileUtil.getImageDir("/" + FRIEND_IMAGE_PATH)
				.toString() + "/";
	}

	private static SavePathUtil instance = new SavePathUtil();

	public static SavePathUtil getInstance() {
		return instance;
	}

	public String getSaveMePath() {
		return SAVE_ME_PATH;
	}

	// public void setSaveMePath(String sAVE_ME_PATH) {
	// SAVE_ME_PATH = sAVE_ME_PATH;
	// }

	public String getSaveFriendPath() {
		return SAVE_FRIEND_PATH;
	}

	// public void setSaveFriendPath(String sAVE_FRIEND_PATH) {
	// SAVE_FRIEND_PATH = sAVE_FRIEND_PATH;
	// }

}
