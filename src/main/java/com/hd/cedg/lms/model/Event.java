package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event implements Comparable<Event>, Serializable {

	private static final long serialVersionUID = 3201701679315167094L;

	public static final int TYPE_ID_OPTION = 1;
	public static final int TYPE_ID_PACKAGE = 2;

	public static final int STATUS_NOT_OPEN_YET = 0;
	public static final int STATUS_OPEN = 1;
	public static final int STATUS_REG_CLOSED = 2;
	public static final int STATUS_ARCHIVED = 3;

	public static final int SORT_TITLE = 1;
	public static final int SORT_DATE = 2;
	public static final int SORT_LOCATION = 3;
	public static final int SORT_ID = 4;
	public static final int SORT_STATUS = 5;
	
	public static final int IN_PERSON = 1;
	public static final int VIRTUAL = 0;
	public static final int IN_PERSON_TRAINING = 2;
	
	public static final int STATUS_ATTENDED = 1; 
	
	public static final int VIRTUAL_TYPE_MEDIASITE = 1;

	private int eventId;

	// Basic Info
	private String title;
	private String description;
	private String imageUrl;
	private int status;
	private DateRange dateRange;
	private boolean enrollOthers;
	private boolean enrollManaged;
	private int capacity;
	private int currentAttendeeCount;
	private String helpPhone;
	private String helpEmail;

	// In-Person or Virtual or In-Person Training
	private int inPerson;

	private String instProgram;
	private int points;
	
	// In-Person: Location Info
	private Venue venue;

	// Virtual: Webcast Type
	private boolean virtualLive;
	private int virtualType;
	private String virtualHelpPhone;
	private String mediaSitePeidSilverlight;
	private String mediaSitePeidClassic;

	// Event Options (classes and additional items)
	private List<EventOption> eventOptions;
	private List<EventOptionPackage> eventOptionPackages;
	private List<EventRuleMutex> eventRuleMutexes;
	private List<EventRuleRequiredSet> eventRuleRequiredSets;
	private List<EventDisplayGroup> eventDisplayGroups;

	// Cost
	private Cost cost;
	private boolean allowCC;
	private List<EventPaymentMethod> paymentMethods;

	// Questions
	private List<EventQuestion> questions;

	// Confirmation Email
	private String confEmailFrom;
	private String confEmailBcc;
	private String confEmailSubject;
	private String confEmailHtml;
	private String confEmailText;

	// Reminder Email
	private String remindEmailFrom;
	private String remindEmailBcc;
	private String remindEmailSubject;
	private String remindEmailHtml;
	private String remindEmailText;

	// Admins
	private List<String> adminUsernames;

	public Event() {
		dateRange = new DateRange();
		enrollOthers = false;
		enrollManaged = false;
		venue = new Venue();
		eventOptions = new ArrayList<EventOption>();
		eventOptionPackages = new ArrayList<EventOptionPackage>();
		eventRuleMutexes = new ArrayList<EventRuleMutex>();
		eventRuleRequiredSets = new ArrayList<EventRuleRequiredSet>();
		eventDisplayGroups = new ArrayList<EventDisplayGroup>();
		cost = new Cost();
		allowCC = true;
		paymentMethods = new ArrayList<EventPaymentMethod>();
		questions = new ArrayList<EventQuestion>();
		adminUsernames = new ArrayList<String>();
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isEnrollOthers() {
		return enrollOthers;
	}

	public void setEnrollOthers(boolean enrollOthers) {
		this.enrollOthers = enrollOthers;
	}

	public boolean isEnrollManaged() {
		return enrollManaged;
	}

	public void setEnrollManaged(boolean enrollManaged) {
		this.enrollManaged = enrollManaged;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getCurrentAttendeeCount() {
		return currentAttendeeCount;
	}

	public void setCurrentAttendeeCount(int currentAttendeeCount) {
		this.currentAttendeeCount = currentAttendeeCount;
	}

	public boolean isFull() {
		if (capacity > 0 && currentAttendeeCount >= capacity) {
			return true;
		} else {
			return false;
		}
	}

	public String getHelpPhone() {
		return helpPhone;
	}

	public void setHelpPhone(String helpPhone) {
		this.helpPhone = helpPhone;
	}

	public String getHelpEmail() {
		return helpEmail;
	}

	public void setHelpEmail(String helpEmail) {
		this.helpEmail = helpEmail;
	}

	public int getInPerson() {
		return inPerson;
	}

	public void setInPerson(int inPerson) {
		this.inPerson = inPerson;
	}

	public String getInstProgram() {
		return instProgram;
	}

	public void setInstProgram(String instProgram) {
		this.instProgram = instProgram;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
	
	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	public boolean isVirtualLive() {
		return virtualLive;
	}

	public void setVirtualLive(boolean virtualLive) {
		this.virtualLive = virtualLive;
	}

	public int getVirtualType() {
		return virtualType;
	}

	public void setVirtualType(int virtualType) {
		this.virtualType = virtualType;
	}

	public String getVirtualHelpPhone() {
		return virtualHelpPhone;
	}

	public void setVirtualHelpPhone(String virtualHelpPhone) {
		this.virtualHelpPhone = virtualHelpPhone;
	}

	public String getMediaSitePeidSilverlight() {
		return mediaSitePeidSilverlight;
	}

	public void setMediaSitePeidSilverlight(String mediaSitePeidSilverlight) {
		this.mediaSitePeidSilverlight = mediaSitePeidSilverlight;
	}

	public String getMediaSitePeidClassic() {
		return mediaSitePeidClassic;
	}

	public void setMediaSitePeidClassic(String mediaSitePeidClassic) {
		this.mediaSitePeidClassic = mediaSitePeidClassic;
	}

	public DateRange getDateRange() {
		return dateRange;
	}

	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}

	public List<EventOption> getEventOptions() {
		return eventOptions;
	}

	public void setEventOptions(List<EventOption> eventOptions) {
		this.eventOptions = eventOptions;
	}

	public List<EventOptionPackage> getEventOptionPackages() {
		return eventOptionPackages;
	}

	public void setEventOptionPackages(
			List<EventOptionPackage> eventOptionPackages) {
		this.eventOptionPackages = eventOptionPackages;
	}

	public List<EventRuleMutex> getEventRuleMutexes() {
		return eventRuleMutexes;
	}

	public void setEventRuleMutexes(List<EventRuleMutex> eventRuleMutexes) {
		this.eventRuleMutexes = eventRuleMutexes;
	}

	public List<EventRuleRequiredSet> getEventRuleRequiredSets() {
		return eventRuleRequiredSets;
	}

	public void setEventRuleRequiredSets(
			List<EventRuleRequiredSet> eventRuleRequiredSets) {
		this.eventRuleRequiredSets = eventRuleRequiredSets;
	}

	public List<EventDisplayGroup> getEventDisplayGroups() {
		return eventDisplayGroups;
	}

	public void setEventDisplayGroups(List<EventDisplayGroup> eventDisplayGroups) {
		this.eventDisplayGroups = eventDisplayGroups;
	}

	public Cost getCost() {
		return cost;
	}

	public void setCost(Cost cost) {
		this.cost = cost;
	}

	public boolean isAllowCC() {
		return allowCC;
	}

	public void setAllowCC(boolean allowCC) {
		this.allowCC = allowCC;
	}

	public List<EventPaymentMethod> getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(List<EventPaymentMethod> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	public List<EventQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<EventQuestion> questions) {
		this.questions = questions;
	}

	public String getConfEmailFrom() {
		return confEmailFrom;
	}

	public void setConfEmailFrom(String confEmailFrom) {
		this.confEmailFrom = confEmailFrom;
	}

	public String getConfEmailBcc() {
		return confEmailBcc;
	}

	public void setConfEmailBcc(String confEmailBcc) {
		this.confEmailBcc = confEmailBcc;
	}

	public String getConfEmailSubject() {
		return confEmailSubject;
	}

	public void setConfEmailSubject(String confEmailSubject) {
		this.confEmailSubject = confEmailSubject;
	}

	public String getConfEmailHtml() {
		return confEmailHtml;
	}

	public void setConfEmailHtml(String confEmailHtml) {
		this.confEmailHtml = confEmailHtml;
	}

	public String getConfEmailText() {
		return confEmailText;
	}

	public void setConfEmailText(String confEmailText) {
		this.confEmailText = confEmailText;
	}

	public String getRemindEmailFrom() {
		return remindEmailFrom;
	}

	public void setRemindEmailFrom(String remindEmailFrom) {
		this.remindEmailFrom = remindEmailFrom;
	}

	public String getRemindEmailBcc() {
		return remindEmailBcc;
	}

	public void setRemindEmailBcc(String remindEmailBcc) {
		this.remindEmailBcc = remindEmailBcc;
	}

	public String getRemindEmailSubject() {
		return remindEmailSubject;
	}

	public void setRemindEmailSubject(String remindEmailSubject) {
		this.remindEmailSubject = remindEmailSubject;
	}

	public String getRemindEmailHtml() {
		return remindEmailHtml;
	}

	public void setRemindEmailHtml(String remindEmailHtml) {
		this.remindEmailHtml = remindEmailHtml;
	}

	public String getRemindEmailText() {
		return remindEmailText;
	}

	public void setRemindEmailText(String remindEmailText) {
		this.remindEmailText = remindEmailText;
	}

	public List<String> getAdminUsernames() {
		return adminUsernames;
	}

	public void setAdminUsernames(List<String> adminUsernames) {
		this.adminUsernames = adminUsernames;
	}

	public String getDisplayName() {
		if (inPerson == IN_PERSON || inPerson == IN_PERSON_TRAINING) {
			if (venue.getOverride() == null || "".equals(venue.getOverride())) {
				return venue.getCity() + ", " + venue.getRegion();
			} else {
				return venue.getOverride();
			}
		} else {
			return "Webcast";
		}
	}

	public int compareTo(Event o) {
		// Start Date
		int startDateCompare = dateRange.getStartDate().compareTo(
				o.getDateRange().getStartDate());
		if (startDateCompare != 0) {
			return startDateCompare;
		}

		// End Date
		Date myEndDate = dateRange.getEndDate() != null ? dateRange
				.getEndDate() : dateRange.getStartDate();
		Date yourEndDate = o.getDateRange().getEndDate() != null ? o
				.getDateRange().getEndDate() : o.getDateRange().getStartDate();
		int endDateCompare = myEndDate.compareTo(yourEndDate);
		if (endDateCompare != 0) {
			return endDateCompare;
		}

		// Start Time
		Date myStartTime = dateRange.getStartTime();
		Date yourStartTime = o.getDateRange().getStartTime();
		if (myStartTime != null && yourStartTime == null) {
			return 1;
		} else if (myStartTime == null && yourStartTime != null) {
			return -1;
		} else if (myStartTime != null && yourStartTime != null) {
			int startTimeCompare = myStartTime.compareTo(yourStartTime);
			if (startTimeCompare != 0) {
				return startTimeCompare;
			}
		}

		// Title
		int titleCompare = title.compareTo(o.getTitle());
		if (titleCompare != 0) {
			return titleCompare;
		}

		// Display Name (Location)
		int displayNameCompare = getDisplayName().compareTo(o.getDisplayName());
		if (displayNameCompare != 0) {
			return displayNameCompare;
		}

		// Event ID
		return eventId - o.getEventId();
	}

	public static void sortEvents(List<Event> events, int sort) {
		boolean alreadySorted = true;

		for (int i = 1; i < events.size(); i++) {
			boolean swap = false;

			if (sort == SORT_TITLE) {
				String a = events.get(i - 1).getTitle();
				String b = events.get(i).getTitle();
				swap = compareString(a, b) > 0;
			} else if (sort == SORT_DATE) {
				Date a = events.get(i - 1).getDateRange().getStartDate();
				Date b = events.get(i).getDateRange().getStartDate();
				swap = compareDate(a, b) < 0;
			} else if (sort == SORT_LOCATION) {
				String a = events.get(i - 1).getDisplayName();
				String b = events.get(i).getDisplayName();
				swap = compareString(a, b) > 0;
			} else if (sort == SORT_ID) {
				int a = events.get(i - 1).getEventId();
				int b = events.get(i).getEventId();
				swap = compareInt(a, b) > 0;
			} else if (sort == SORT_STATUS) {
				int a = events.get(i - 1).getStatus();
				int b = events.get(i).getStatus();
				swap = compareInt(a, b) > 0;
			}

			if (swap) {
				Event temp = events.get(i - 1);
				events.set(i - 1, events.get(i));
				events.set(i, temp);

				alreadySorted = false;
			}
		}

		if (!alreadySorted) {
			sortEvents(events, sort);
		}
	}

	private static int compareString(String a, String b) {
		if (a == null) {
			if (b == null) {
				return 0;
			} else {
				return 1;
			}
		} else {
			if (b == null) {
				return -1;
			} else {
				return a.compareTo(b);
			}
		}
	}

	private static int compareDate(Date a, Date b) {
		if (a == null) {
			if (b == null) {
				return 0;
			} else {
				return 1;
			}
		} else {
			if (b == null) {
				return -1;
			} else {
				return a.compareTo(b);
			}
		}
	}

	private static int compareInt(int a, int b) {
		return a - b;
	}
}