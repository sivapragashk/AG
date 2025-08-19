package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Course extends Permissioned implements Serializable,
		Comparable<Course> {

	private static final long serialVersionUID = -3785571323306716241L;

	public static final String PERMISSION_TYPE = "Course";
	public static final String ACCESSTO_EVERYONE_IN_THE_WORLD = "everyone-in-world";

	private int courseId;
	private String learningId;
	private String name;
	private String subtitle;
	private String description;
	private String imageUrl;
	private String commercialStaticImage;
	private String commercialProtocol;
	private String commercialRtmpServer;
	private String commercialRtmpFile;
	private String commercialHttpVideoUrl;
	private String commercialAspectRatio;
	private String length;
	private float cost;
	private Date courseDate;
	private String displayType;
	private boolean statusVisible;
	private String launchType;
	private String directUrl;
	private boolean available;
	private boolean archived;
	private boolean catalogPublish;
	private List<CourseMaterial> materials;
	private List<CoursePerson> coursePeople;

	private List<String> prereqIds;

	private List<String> relatedProducts;
	private List<String> topics;

	private List<CourseVideo> courseVideos;
	private List<CourseEvent> courseEvents;

	private List<String> accessTags;
	
	private List<Integer> dockTags;

	public Course() {
		statusVisible = true;
		available = true;
		archived = false;
		catalogPublish = true;
		materials = new ArrayList<CourseMaterial>();
		coursePeople = new ArrayList<CoursePerson>();
		prereqIds = new ArrayList<String>();
		relatedProducts = new ArrayList<String>();
		topics = new ArrayList<String>();
		courseVideos = new ArrayList<CourseVideo>();
		courseEvents = new ArrayList<CourseEvent>();
		accessTags = new ArrayList<String>();
		dockTags = new ArrayList<Integer>();
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public String getLearningId() {
		return learningId;
	}

	public void setLearningId(String learningId) {
		this.learningId = learningId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getCommercialStaticImage() {
		return commercialStaticImage;
	}

	public void setCommercialStaticImage(String commercialStaticImage) {
		this.commercialStaticImage = commercialStaticImage;
	}

	public String getCommercialProtocol() {
		return commercialProtocol;
	}

	public void setCommercialProtocol(String commercialProtocol) {
		this.commercialProtocol = commercialProtocol;
	}

	public String getCommercialRtmpServer() {
		return commercialRtmpServer;
	}

	public void setCommercialRtmpServer(String commercialRtmpServer) {
		this.commercialRtmpServer = commercialRtmpServer;
	}

	public String getCommercialRtmpFile() {
		return commercialRtmpFile;
	}

	public void setCommercialRtmpFile(String commercialRtmpFile) {
		this.commercialRtmpFile = commercialRtmpFile;
	}

	public String getCommercialHttpVideoUrl() {
		return commercialHttpVideoUrl;
	}

	public void setCommercialHttpVideoUrl(String commercialHttpVideoUrl) {
		this.commercialHttpVideoUrl = commercialHttpVideoUrl;
	}

	public String getCommercialAspectRatio() {
		return commercialAspectRatio;
	}

	public void setCommercialAspectRatio(String commercialAspectRatio) {
		this.commercialAspectRatio = commercialAspectRatio;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public float getCost() {
		return cost;
	}

	public String getFormattedCost(boolean units) {
		DecimalFormat costFormat = new DecimalFormat("########0.00");
		return (units ? "$" : "") + costFormat.format(cost);
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public Date getCourseDate() {
		return courseDate;
	}

	public String getFormattedCourseDate(String dateFormat) {
		if (courseDate != null) {
			return new SimpleDateFormat(dateFormat).format(courseDate);
		} else {
			return "";
		}
	}

	public void setCourseDate(Date courseDate) {
		this.courseDate = courseDate;
	}

	public void setCourseDate(String courseDate, String dateFormat) {
		if (courseDate != null) {
			try {
				this.courseDate = new SimpleDateFormat(dateFormat)
						.parse(courseDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public String getDisplayType() {
		return displayType;
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}

	public boolean isStatusVisible() {
		return statusVisible;
	}

	public void setStatusVisible(boolean statusVisible) {
		this.statusVisible = statusVisible;
	}

	public String getLaunchType() {
		return launchType;
	}

	public void setLaunchType(String launchType) {
		this.launchType = launchType;
	}

	public String getDirectUrl() {
		return directUrl;
	}

	public void setDirectUrl(String directUrl) {
		this.directUrl = directUrl;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public boolean isCatalogPublish() {
		return catalogPublish;
	}

	public void setCatalogPublish(boolean catalogPublish) {
		this.catalogPublish = catalogPublish;
	}

	public List<CourseMaterial> getMaterials() {
		return materials;
	}

	public void setMaterials(List<CourseMaterial> materials) {
		this.materials = materials;
	}

	public List<CoursePerson> getCoursePeople() {
		return coursePeople;
	}

	public void setCoursePeople(List<CoursePerson> coursePeople) {
		this.coursePeople = coursePeople;
	}

	public List<String> getPrereqIds() {
		return prereqIds;
	}

	public void setPrereqIds(List<String> prereqIds) {
		this.prereqIds = prereqIds;
	}

	public List<String> getRelatedProducts() {
		return relatedProducts;
	}

	public void setRelatedProducts(List<String> relatedProducts) {
		this.relatedProducts = relatedProducts;
	}

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

	public List<CourseVideo> getCourseVideos() {
		return courseVideos;
	}

	public void setCourseVideos(List<CourseVideo> courseVideos) {
		this.courseVideos = courseVideos;
	}

	public List<CourseEvent> getCourseEvents() {
		return courseEvents;
	}

	public void setCourseEvents(List<CourseEvent> courseEvents) {
		this.courseEvents = courseEvents;
	}

	public int compareTo(Course lm) {
		return name.toUpperCase().compareTo(lm.getName().toUpperCase());
	}

	public String getPermissionType() {
		return PERMISSION_TYPE;
	}

	public List<String> getAccessTags() {
		return accessTags;
	}

	public void setAccessTags(List<String> accessTags) {
		this.accessTags = accessTags;
	}
	
	public List<Integer> getDockTags() {
		return dockTags;
	}

	public void setDockTags(List<Integer> dockTags) {
		this.dockTags = dockTags;
	}
}
