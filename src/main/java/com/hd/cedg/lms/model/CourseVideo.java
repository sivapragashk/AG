package com.hd.cedg.lms.model;

import java.io.Serializable;

public class CourseVideo implements Serializable {

	private static final long serialVersionUID = 7562348575992609958L;

	private String title;
	private String description;
	private String protocol;
	private String rtmpServer;
	private String rtmpFile;
	private String httpVideoUrl;
	private String imageUrl;
	private String aspectRatio;
	private String watermarkImageUrl;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getRtmpServer() {
		return rtmpServer;
	}

	public void setRtmpServer(String rtmpServer) {
		this.rtmpServer = rtmpServer;
	}

	public String getRtmpFile() {
		return rtmpFile;
	}

	public void setRtmpFile(String rtmpFile) {
		this.rtmpFile = rtmpFile;
	}

	public String getHttpVideoUrl() {
		return httpVideoUrl;
	}

	public void setHttpVideoUrl(String httpVideoUrl) {
		this.httpVideoUrl = httpVideoUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(String aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	public String getWatermarkImageUrl() {
		return watermarkImageUrl;
	}

	public void setWatermarkImageUrl(String watermarkImageUrl) {
		this.watermarkImageUrl = watermarkImageUrl;
	}

}
