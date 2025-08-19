package com.hd.cedg.lms.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hd.cedg.lms.model.Course;
import com.hd.cedg.lms.model.CourseEvent;
import com.hd.cedg.lms.model.CourseMaterial;
import com.hd.cedg.lms.model.CoursePerson;
import com.hd.cedg.lms.model.CourseVideo;
import com.hd.cedg.lms.model.ToDoList;
import com.hd.cedg.lms.model.ToDoListSet;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;


public class CourseDAO extends BaseLmsDAO{

	PermissionDAO permissionDAO;

	public CourseDAO() {
		permissionDAO = new PermissionDAO();
	}
	
	public Map<String, Course> retrieveMapByToDoList(ToDoList toDoList) {
		Map<String, Course> courseMap = new HashMap<String, Course>();
		for (ToDoListSet listSet : toDoList.getListSets()) {
			for (String learningId : listSet.getLearningIds()) {
				Course course = retrieve(learningId);
				if (course != null) {
					courseMap.put(learningId, course);
				}
			}
		}
		return courseMap;
	}
	
	public Course retrieve(String learningId) {
		Course course = null;

		try {
			String sql = "select c.courseid, c.learningid, c.name, c.subtitle, c.description, c.imageurl, c.commercialstaticimage, c.commercialprotocol, c.commercialrtmpserver, c.commercialrtmpfile, c.commercialhttpvideourl, c.commercialaspectratio, c.length, c.cost, to_char(c.coursedate, 'MM/DD/YYYY') coursedate, disp.name displaytype, c.statusvisible, laun.name launchtype, c.directurl, c.available, c.archived, c.catalogpublish from lc_course c inner join lc_displaytype disp on disp.displaytypeid = c.displaytypeid inner join lc_launchtype laun on laun.launchtypeid = c.launchtypeid where learningid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(learningId);
			course = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Course>() {
						public Course handleResultSet(ResultSet rs)
								throws Exception {
							Course course = null;
							if (rs.next()) {
								course = new Course();
								int courseId = rs.getInt("courseid");
								course.setCourseId(courseId);
								course
										.setLearningId(rs
												.getString("learningid"));
								course.setName(rs.getString("name"));
								course.setSubtitle(rs.getString("subtitle"));
								course.setDescription(rs
										.getString("description"));
								course.setImageUrl(rs.getString("imageurl"));
								course.setCommercialStaticImage(rs
										.getString("commercialstaticimage"));
								course.setCommercialProtocol(rs
										.getString("commercialprotocol"));
								course.setCommercialRtmpServer(rs
										.getString("commercialrtmpserver"));
								course.setCommercialRtmpFile(rs
										.getString("commercialrtmpfile"));
								course.setCommercialHttpVideoUrl(rs
										.getString("commercialhttpvideourl"));
								course.setCommercialAspectRatio(rs
										.getString("commercialaspectratio"));
								course.setLength(rs.getString("length"));
								course.setCost(rs.getFloat("cost"));
								course.setCourseDate(
										rs.getString("coursedate"),
										"MM/dd/yyyy");
								course.setDisplayType(rs
										.getString("displaytype"));
								course.setStatusVisible(rs
										.getInt("statusvisible") != 0);
								course
										.setLaunchType(rs
												.getString("launchtype"));
								course.setDirectUrl(rs.getString("directurl"));
								course
										.setAvailable(rs.getInt("available") != 0);
								course.setArchived(rs.getInt("archived") != 0);
								course.setCatalogPublish(rs
										.getInt("catalogpublish") != 0);

								// Course Materials
								List<CourseMaterial> materials = retrieveCourseMaterials(courseId);
								course.setMaterials(materials);

								// Course People
								List<CoursePerson> coursePeople = retrieveCoursePeople(courseId);
								course.setCoursePeople(coursePeople);

								// Prereqs
								List<String> prereqIds = retrievePrereqs(courseId);
								course.setPrereqIds(prereqIds);

								// Related Products
								List<String> relatedProducts = retrieveRelatedProducts(courseId);
								course.setRelatedProducts(relatedProducts);

								// Topics
								List<String> topics = retrieveTopics(courseId);
								course.setTopics(topics);

								// Course Videos
								List<CourseVideo> courseVideos = retrieveCourseVideos(courseId);
								course.setCourseVideos(courseVideos);

								// Course Events
								List<CourseEvent> courseEvents = retrieveCourseEvents(courseId);
								course.setCourseEvents(courseEvents);

								
								// Dock Tags
								List<Integer> dockTags = permissionDAO.retrieveDockTags(course
												.getPermissionType(), course.getCourseId());
								course.setDockTags(dockTags);
								
								// Access Tags
								List<String> accessTags = permissionDAO
										.retrieveAccessTags(course
												.getPermissionType(), course
												.getCourseId());
								course.setAccessTags(accessTags);
							}
							return course;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Course \"" + learningId
					+ "\": " + e.getMessage());
			e.printStackTrace();
		}

		return course;
	}
	
	private List<CourseMaterial> retrieveCourseMaterials(int courseId) {
		List<CourseMaterial> materials = new ArrayList<CourseMaterial>();

		try {
			String sql = "select name, url from lc_coursematerial where courseid = ? order by name";
			List<Object> params = new ArrayList<Object>();
			params.add(courseId);
			materials = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<CourseMaterial>>() {
						public List<CourseMaterial> handleResultSet(ResultSet rs)
								throws Exception {
							List<CourseMaterial> materials = new ArrayList<CourseMaterial>();
							while (rs.next()) {
								CourseMaterial material = new CourseMaterial();
								material.setName(rs.getString("name"));
								material.setUrl(rs.getString("url"));
								materials.add(material);
							}
							return materials;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Course Materials for Course "
					+ courseId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return materials;
	}
	
	private List<CoursePerson> retrieveCoursePeople(int courseId) {
		List<CoursePerson> coursePeople = new ArrayList<CoursePerson>();

		try {
			String sql = "select typename, name, title, imageurl, biotext from lc_courseperson where courseid = ? order by name";
			List<Object> params = new ArrayList<Object>();
			params.add(courseId);
			coursePeople = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<CoursePerson>>() {
						public List<CoursePerson> handleResultSet(ResultSet rs)
								throws Exception {
							List<CoursePerson> coursePeople = new ArrayList<CoursePerson>();
							while (rs.next()) {
								CoursePerson coursePerson = new CoursePerson();
								coursePerson.setTypeName(rs
										.getString("typename"));
								coursePerson.setName(rs.getString("name"));
								coursePerson.setTitle(rs.getString("title"));
								coursePerson.setImageUrl(rs
										.getString("imageurl"));
								coursePerson
										.setBioText(rs.getString("biotext"));
								coursePeople.add(coursePerson);
							}
							return coursePeople;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Course People for Course "
					+ courseId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return coursePeople;
	}
	
	private List<String> retrievePrereqs(int courseId) {
		List<String> prereqIds = new ArrayList<String>();

		try {
			String sql = "select prereqid from lc_prereqs where courseid = ? order by sortorder";
			List<Object> params = new ArrayList<Object>();
			params.add(courseId);
			prereqIds = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<String>>() {
						public List<String> handleResultSet(ResultSet rs)
								throws Exception {
							List<String> prereqIds = new ArrayList<String>();
							while (rs.next()) {
								String prereqId = rs.getString("prereqid");
								prereqIds.add(prereqId);
							}
							return prereqIds;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Prereq IDs for Course "
					+ courseId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return prereqIds;
	}
	
	private List<String> retrieveRelatedProducts(int courseId) {
		List<String> relatedProducts = new ArrayList<String>();

		try {
			String sql = "select productname from lc_courseproduct where courseid = ? order by productname";
			List<Object> params = new ArrayList<Object>();
			params.add(courseId);
			relatedProducts = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<String>>() {
						public List<String> handleResultSet(ResultSet rs)
								throws Exception {
							List<String> relatedProducts = new ArrayList<String>();
							while (rs.next()) {
								String relatedProduct = rs
										.getString("productname");
								relatedProducts.add(relatedProduct);
							}
							return relatedProducts;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Related Products for Course "
					+ courseId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return relatedProducts;
	}
	
	private List<String> retrieveTopics(int courseId) {
		List<String> topics = new ArrayList<String>();

		try {
			String sql = "select topicname from lc_coursetopic where courseid = ? order by topicname";
			List<Object> params = new ArrayList<Object>();
			params.add(courseId);
			topics = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<String>>() {
						public List<String> handleResultSet(ResultSet rs)
								throws Exception {
							List<String> topics = new ArrayList<String>();
							while (rs.next()) {
								String topic = rs.getString("topicname");
								topics.add(topic);
							}
							return topics;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Topics for Course " + courseId
					+ ": " + e.getMessage());
			e.printStackTrace();
		}

		return topics;
	}
	
	private List<CourseVideo> retrieveCourseVideos(int courseId) {
		List<CourseVideo> courseVideos = new ArrayList<CourseVideo>();

		try {
			String sql = "select title, description, protocol, rtmpserver, rtmpfile, httpvideourl, imageurl, aspectratio, watermarkimageurl from lc_coursevideo where courseid = ? order by sortorder";
			List<Object> params = new ArrayList<Object>();
			params.add(courseId);
			courseVideos = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<CourseVideo>>() {
						public List<CourseVideo> handleResultSet(ResultSet rs)
								throws Exception {
							List<CourseVideo> courseVideos = new ArrayList<CourseVideo>();
							while (rs.next()) {
								CourseVideo courseVideo = new CourseVideo();
								courseVideo.setTitle(rs.getString("title"));
								courseVideo.setDescription(rs
										.getString("description"));
								courseVideo.setProtocol(rs
										.getString("protocol"));
								courseVideo.setRtmpServer(rs
										.getString("rtmpserver"));
								courseVideo.setRtmpFile(rs
										.getString("rtmpfile"));
								courseVideo.setHttpVideoUrl(rs
										.getString("httpvideourl"));
								courseVideo.setImageUrl(rs
										.getString("imageurl"));
								courseVideo.setAspectRatio(rs
										.getString("aspectratio"));
								courseVideo.setWatermarkImageUrl(rs
										.getString("watermarkimageurl"));
								courseVideos.add(courseVideo);
							}
							return courseVideos;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Videos for Course " + courseId
					+ ": " + e.getMessage());
			e.printStackTrace();
		}

		return courseVideos;
	}
	
	public List<Integer> retrieveCourseEvents(String learningId) {
		List<Integer> courseEvents = new ArrayList<Integer>();

		try {
			String sql = "select ce.eventid from lc_course co left join lc_courseevent ce on ce.courseid = co.courseid where co.learningid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(learningId);
			courseEvents = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<Integer>>() {
						public List<Integer> handleResultSet(ResultSet rs)
								throws Exception {
							List<Integer> courseEvents = new ArrayList<Integer>();
							while (rs.next()) {
								courseEvents.add(rs.getInt("eventid"));
							}
							return courseEvents;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Events for Learningid " + learningId
					+ ": " + e.getMessage());
			e.printStackTrace();
		}

		return courseEvents;
	}
	
	private List<CourseEvent> retrieveCourseEvents(int courseId) {
		List<CourseEvent> courseEvents = new ArrayList<CourseEvent>();

		try {
			String sql = "select eventid, emphasize from lc_courseevent where courseid = ? order by eventid";
			List<Object> params = new ArrayList<Object>();
			params.add(courseId);
			courseEvents = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<CourseEvent>>() {
						public List<CourseEvent> handleResultSet(ResultSet rs)
								throws Exception {
							List<CourseEvent> courseEvents = new ArrayList<CourseEvent>();
							while (rs.next()) {
								CourseEvent courseEvent = new CourseEvent();
								courseEvent.setEventId(rs.getInt("eventid"));
								courseEvent
										.setEmphasize(rs.getInt("emphasize") != 0);
								courseEvents.add(courseEvent);
							}
							return courseEvents;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Events for Course " + courseId
					+ ": " + e.getMessage());
			e.printStackTrace();
		}

		return courseEvents;
	}

	public String retrieveEkpEventId(String learningId) {
		String eventId = null;

		try {
			String sql = "select csched.eventid from learningobject lo left join cschedule csched on csched.courseid = lo.learningid where lo.learningid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(learningId);
			eventId = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<String>() {
						public String handleResultSet(ResultSet rs)
								throws Exception {
							String eventId = null;
							if (rs.next()) {
								eventId = rs.getString("eventid");
							}
							return eventId;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving EKP Event ID for Course "
					+ learningId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return eventId;
	}
}
