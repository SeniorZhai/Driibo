package com.zoe.driibo.type;

public enum Category {
//	imageAndText("图文"),text("纯文"),image("纯图"),news("最新"),feed("精华");
	popular("Popular"), everyone("Everyone"), debuts("Debuts");
	private String mDisplayName;
	private Category(String displayName) {
		mDisplayName = displayName;
	}
	
	public String getDisplayName() {
        return mDisplayName;
    }
}
