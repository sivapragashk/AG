package com.hd.cedg.lms.bizsvc;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import com.hd.cedg.lms.model.AdminAccessRights;
import com.hd.cedg.lms.model.LearningUser;
import com.hd.cedg.lms.model.OnyxLMSFieldMapper;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class LearningUserBizSvc {

	private static final String ONYX_PROD_DATA_SOURCE = "jdbc/OnyxWriteTLCDS";
	private static final String LC_DATA_SOURCE = "jdbc/EkpDS";
	private OnyxLMSFieldMapper onyxLCFieldMapper;

	private boolean writeToOnyx = false;
	private String hdCatalogXmlRpcUrl;
	private String hdCatalogXmlRpcAccessKey;
	private Map<String, List<String>> accessTagValueMap;

	public LearningUserBizSvc(boolean writeToOnyx, String hdCatalogXmlRpcUrl, String hdCatalogXmlRpcAccessKey, String hdCatalogTranslateDefAccessTags) {
		this.writeToOnyx = writeToOnyx;
		this.hdCatalogXmlRpcUrl = hdCatalogXmlRpcUrl;
		this.hdCatalogXmlRpcAccessKey = hdCatalogXmlRpcAccessKey;
		accessTagValueMap = getAccessTagValueMap(hdCatalogTranslateDefAccessTags);

		onyxLCFieldMapper = new OnyxLMSFieldMapper();
		onyxLCFieldMapper.define("FabProgram", "PFP", "Precertified");
		onyxLCFieldMapper.define("FabProgram", "CSC", "Certified");
		onyxLCFieldMapper.define("FabProgram", null, "None");
		onyxLCFieldMapper.define("DealerProgram", "PDP", "Precertified");
		onyxLCFieldMapper.define("DealerProgram", "CPD", "Certified");
		onyxLCFieldMapper.define("DealerProgram", null, "None");
		onyxLCFieldMapper.define("InstallerProgram", "PIP", "PIP");
		onyxLCFieldMapper.define("InstallerProgram", "CI", "CI");
		onyxLCFieldMapper.define("InstallerProgram", "MI", "MI");
		onyxLCFieldMapper.define("InstallerProgram", "CI_RENEW", "cirenew");
		onyxLCFieldMapper.define("InstallerProgram", "MI_RENEW", "mirenew");
		onyxLCFieldMapper.define("InstallerProgram", null, "None");
	}

	private Map<String, List<String>> getAccessTagValueMap(
			String definitionFileLocation) {
		Map<String, List<String>> valueMap = new HashMap<String, List<String>>();

		DataInputStream in = null;
		try {
			FileInputStream fstream = new FileInputStream(
					definitionFileLocation);
			in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			// Read the first line & throw it out
			br.readLine();
			String line;
			while ((line = br.readLine()) != null) {
				// Parse the Line
				String[] lineParts = line.split("==");
				if (lineParts.length == 2 && !lineParts[0].equals("ELSE")) {
					String reqValuesStr = lineParts[1];
					List<String> reqValues = new ArrayList<String>();
					for (String reqValue : reqValuesStr.split("\\|")) {
						reqValues.add(reqValue);
					}

					String resultValuesStr = lineParts[0];
					List<String> resultValues = new ArrayList<String>();
					for (String resultValue : resultValuesStr.split("\\|")) {
						resultValues.add(resultValue);
					}

					for (String reqValue : reqValues) {
						List<String> existingValues = valueMap.get(reqValue);
						if (existingValues == null) {
							existingValues = new ArrayList<String>();
							valueMap.put(reqValue, existingValues);
						}
						existingValues.addAll(resultValues);
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Error: Couldn't read from file \""
					+ definitionFileLocation + "\"");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return valueMap;
	}

	public LearningUser retrieveSync(int userId) {
		LearningUser user = retrieveOnyx(userId);
		if (user != null) {
			if (user.isActive()) {
				user.setAccessTags(retrieveAccessTags(user.getUsername()));
			}
			boolean overrode = supplimentalRetrieveLc(user);
			sync(user);
			if (overrode) {
				upstreamSync(user);
			}
		}
		return user;
	}

	private LearningUser retrieveOnyx(int userId) {
		LearningUser user = null;

		try {
			//String sql = "SELECT tlc.iIndividualId, CASE WHEN tlc.iCompanyId IS NULL THEN 'Inactive - No Company' WHEN tlc.vchEmailAddress = '' THEN 'Inactive - No Email' WHEN tlc.hasSsoPassword = 0 THEN 'Inactive - No SSO Credentials' WHEN tlc.companyType IS NULL THEN 'Inactive - Non-valid Company Type' ELSE 'Active' END AS status, tlc.vchFirstName, tlc.vchLastName, tlc.vchEmailAddress, COALESCE(tlc.vchAddress1, '') AS vchAddress1, COALESCE(tlc.vchCity, '') AS vchCity, COALESCE(tlc.chRegionCode, '') AS chRegionCode, COALESCE(tlc.vchPostCode, '') AS vchPostCode, CASE WHEN tlc.chCountryCode IS NULL THEN 'None' WHEN tlc.chCountryCode = 'US' THEN 'US' WHEN tlc.chCountryCode = 'CA' THEN 'CA' ELSE 'International' END AS chCountryCode, tlc.vchPhoneNumber, tlc.fabProgram AS fabProg, tlc.dealerProgram AS dealerProg, tlc.installerProgram AS instProg, COALESCE(tlc.iCompanyId, '') AS iCompanyId, COALESCE(tlc.vchCompanyName, '') AS vchCompanyName, COALESCE(tlc.companyType, '') AS compType, CASE WHEN tlc.companyType = 'Dealer' THEN COALESCE(tlc.companySubType, 'Non-Aligned') ELSE COALESCE(tlc.companySubType, '') END AS compSubType, CASE WHEN tlc.isCenturion = 1 THEN 'Centurion' ELSE '' END AS centurion, CASE WHEN tlc.lastGalleryNominationAction = 'Committed' THEN 'Gallery' WHEN tlc.lastShowcaseNominationAction = 'Committed' THEN 'Showcase' ELSE 'None' END AS allianceCommitted, COALESCE(CAST(tlc.fabricatorId AS nvarchar(10)), '0') AS fabricatorId, COALESCE(Fab.vchCompanyName, '') fabricatorName, COALESCE(FabType.vchParameterDesc, '') fabricatorType, COALESCE(CAST(Rep.iIndividualId AS nvarchar(10)), '0') AS salesRepId, COALESCE(Rep.vchFirstName + ' ' + Rep.vchLastName, '') salesRepName, CASE WHEN cdi.iSysContactId IS NOT NULL THEN 'Yes' WHEN cdica.iSysContactId IS NOT NULL THEN 'Yes' ELSE 'No' END cdi, COALESCE(franchise.vchFamilyName, 'None') franchise, tlc.isManagerContact, CASE WHEN cmsCC.iOwnerId IS NOT NULL THEN 1 ELSE 0 END AS cmsTrainingReady, CASE WHEN tradeAlliance.iOwnerId IS NOT NULL THEN 1 ELSE 0 END AS tradeAlliance, CASE WHEN serviceCenter.iOwnerId IS NOT NULL THEN 1 ELSE 0 END AS serviceCenter FROM compass.TheLearningCenterUserView tlc (nolock) LEFT JOIN Company Fab (nolock) ON Fab.iCompanyId = tlc.fabricatorId LEFT JOIN ReferenceParameters FabType (nolock) ON FabType.iParameterId = Fab.iCompanySubTypeCode LEFT JOIN Contact RepCon (nolock) ON RepCon.iOwnerId = tlc.iCompanyId AND RepCon.iContactTypeId = 94 /* Sponsoring Sales Rep */ AND RepCon.tiRecordStatus = 1 LEFT JOIN Individual Rep (nolock) ON Rep.iIndividualId = RepCon.iContactId LEFT JOIN Contact cdi (nolock) ON cdi.iContactId = tlc.iIndividualId AND cdi.iOwnerId = 256806 AND cdi.iContactTypeId = 5 AND cdi.tiRecordStatus = 1 LEFT JOIN Contact cdica (nolock) ON cdica.iContactId = tlc.iIndividualId AND cdica.iOwnerId = 2325689 AND cdica.iContactTypeId = 5 AND cdica.tiRecordStatus = 1 LEFT JOIN Company comp (nolock) ON comp.iCompanyId = tlc.iCompanyId LEFT JOIN CompanyFamily franchise (nolock) ON franchise.iFamilyId = comp.iFamilyId AND franchise.tiRecordStatus = 1 LEFT JOIN ( CustomerCampaign AS cmsCC (nolock) INNER JOIN TrackingCode AS cmsTC (nolock) ON cmsTC.iTrackingId = cmsCC.iTrackingId AND cmsTC.chTrackingCode = 'certMtrDealer' AND cmsTC.tiRecordStatus = 1) ON cmsCC.tiRecordStatus = 1 AND EXISTS ( SELECT cmsCon.iContactID FROM Contact AS cmsCon (nolock) WHERE cmsCon.iOwnerId = cmsCC.iOwnerId AND cmsCon.iContactId = tlc.iIndividualId AND cmsCon.iContactTypeId IN (5, 69) AND cmsCon.tiRecordStatus = 1) AND EXISTS ( SELECT cmsCCA.iCampaignId FROM CustomerCampaignAction AS cmsCCA (nolock) INNER JOIN CampaignAction AS cmsCA (nolock) ON cmsCA.iActionId = cmsCCA.iActionID AND cmsCA.chActionName = 'Signed Letter of Understanding' AND cmsCA.tiRecordStatus = 1 WHERE cmsCCA.iCampaignId = cmsCC.iCampaignId AND cmsCCA.tiRecordStatus = 1) LEFT JOIN compass.hdprogram AS tradeAlliance (nolock) ON tradeAlliance.iOwnerId = tlc.iCompanyId AND tradeAlliance.program = 'Trade Alliance' LEFT JOIN compass.hdprogram AS serviceCenter (nolock) ON serviceCenter.iOwnerId = tlc.iCompanyId AND serviceCenter.program = 'Service Center' WHERE tlc.iIndividualId = ? ORDER BY tlc.iIndividualId";
			String sql = "SELECT tlc.iIndividualId, CASE WHEN tlc.iCompanyId IS NULL THEN 'Inactive - No Company' WHEN tlc.vchEmailAddress = '' THEN 'Inactive - No Email' WHEN tlc.hasSsoPassword = 0 THEN 'Inactive - No SSO Credentials' WHEN tlc.companyType IS NULL THEN 'Inactive - Non-valid Company Type' ELSE 'Active' END AS status, tlc.vchFirstName, tlc.vchLastName, tlc.vchEmailAddress, COALESCE(tlc.vchAddress1, '') AS vchAddress1, COALESCE(tlc.vchCity, '') AS vchCity, COALESCE(tlc.chRegionCode, '') AS chRegionCode, COALESCE(tlc.vchPostCode, '') AS vchPostCode, CASE WHEN tlc.chCountryCode IS NULL THEN 'None' WHEN tlc.chCountryCode = 'US' THEN 'US' WHEN tlc.chCountryCode = 'CA' THEN 'CA' ELSE 'International' END AS chCountryCode, tlc.vchPhoneNumber, tlc.fabProgram AS fabProg, tlc.dealerProgram AS dealerProg, tlc.installerProgram AS instProg, COALESCE(tlc.iCompanyId, '') AS iCompanyId, COALESCE(tlc.vchCompanyName, '') AS vchCompanyName, COALESCE(tlc.companyType, '') AS compType, CASE WHEN tlc.companyType = 'Dealer' THEN COALESCE(tlc.companySubType, 'Non-Aligned') ELSE COALESCE(tlc.companySubType, '') END AS compSubType, CASE WHEN tlc.isCenturion = 1 THEN 'Centurion' ELSE '' END AS centurion, CASE WHEN tlc.lastGalleryNominationAction = 'Committed' THEN 'Gallery' WHEN tlc.lastShowcaseNominationAction = 'Committed' THEN 'Showcase' ELSE 'None' END AS allianceCommitted, COALESCE(CAST(tlc.fabricatorId AS nvarchar(10)), '0') AS fabricatorId, COALESCE(Fab.vchCompanyName, '') fabricatorName, COALESCE(FabType.vchParameterDesc, '') fabricatorType, COALESCE(CAST(Rep.iIndividualId AS nvarchar(10)), '0') AS salesRepId, COALESCE(Rep.vchFirstName + ' ' + Rep.vchLastName, '') salesRepName, CASE WHEN cdi.iSysContactId IS NOT NULL THEN 'Yes' WHEN cdica.iSysContactId IS NOT NULL THEN 'Yes' ELSE 'No' END cdi, COALESCE(franchise.vchFamilyName, 'None') franchise, tlc.isManagerContact, CASE WHEN cmsCC.iOwnerId IS NOT NULL THEN 1 ELSE 0 END AS cmsTrainingReady, CASE WHEN tradeAlliance.iOwnerId IS NOT NULL THEN 1 ELSE 0 END AS tradeAlliance, CASE WHEN serviceCenter.iOwnerId IS NOT NULL THEN 1 ELSE 0 END AS serviceCenter, custphone.vchPhoneNumber as cellphone FROM compass.TheLearningCenterUserView tlc (nolock) LEFT JOIN CustomerPhone custphone (nolock) ON custphone.iOwnerID = tlc.iIndividualId and iPhoneTypeId = 103 LEFT JOIN Company Fab (nolock) ON Fab.iCompanyId = tlc.fabricatorId LEFT JOIN ReferenceParameters FabType (nolock) ON FabType.iParameterId = Fab.iCompanySubTypeCode LEFT JOIN Contact RepCon (nolock) ON RepCon.iOwnerId = tlc.iCompanyId AND RepCon.iContactTypeId = 94 /* Sponsoring Sales Rep */ AND RepCon.tiRecordStatus = 1 LEFT JOIN Individual Rep (nolock) ON Rep.iIndividualId = RepCon.iContactId LEFT JOIN Contact cdi (nolock) ON cdi.iContactId = tlc.iIndividualId AND cdi.iOwnerId = 256806 AND cdi.iContactTypeId = 5 AND cdi.tiRecordStatus = 1 LEFT JOIN Contact cdica (nolock) ON cdica.iContactId = tlc.iIndividualId AND cdica.iOwnerId = 2325689 AND cdica.iContactTypeId = 5 AND cdica.tiRecordStatus = 1 LEFT JOIN Company comp (nolock) ON comp.iCompanyId = tlc.iCompanyId LEFT JOIN CompanyFamily franchise (nolock) ON franchise.iFamilyId = comp.iFamilyId AND franchise.tiRecordStatus = 1 LEFT JOIN ( CustomerCampaign AS cmsCC (nolock) INNER JOIN TrackingCode AS cmsTC (nolock) ON cmsTC.iTrackingId = cmsCC.iTrackingId AND cmsTC.chTrackingCode = 'certMtrDealer' AND cmsTC.tiRecordStatus = 1) ON cmsCC.tiRecordStatus = 1 AND EXISTS ( SELECT cmsCon.iContactID FROM Contact AS cmsCon (nolock) WHERE cmsCon.iOwnerId = cmsCC.iOwnerId AND cmsCon.iContactId = tlc.iIndividualId AND cmsCon.iContactTypeId IN (5, 69) AND cmsCon.tiRecordStatus = 1) AND EXISTS ( SELECT cmsCCA.iCampaignId FROM CustomerCampaignAction AS cmsCCA (nolock) INNER JOIN CampaignAction AS cmsCA (nolock) ON cmsCA.iActionId = cmsCCA.iActionID AND cmsCA.chActionName = 'Signed Letter of Understanding' AND cmsCA.tiRecordStatus = 1 WHERE cmsCCA.iCampaignId = cmsCC.iCampaignId AND cmsCCA.tiRecordStatus = 1) LEFT JOIN compass.hdprogram AS tradeAlliance (nolock) ON tradeAlliance.iOwnerId = tlc.iCompanyId AND tradeAlliance.program = 'Trade Alliance' LEFT JOIN compass.hdprogram AS serviceCenter (nolock) ON serviceCenter.iOwnerId = tlc.iCompanyId AND serviceCenter.program = 'Service Center' WHERE tlc.iIndividualId = ? ORDER BY tlc.iIndividualId";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			user = DataUtils.query(sql, params, ONYX_PROD_DATA_SOURCE,
					new ResultSetHandler<LearningUser>() {
						public LearningUser handleResultSet(ResultSet rs)
								throws Exception {
							List<LearningUser> users = resultSetToUserList(rs);
							if (users.size() > 0) {
								return users.get(0);
							} else {
								return null;
							}
						}
					});

		} catch (Exception e) {
			System.err.println("Error retrieving user " + userId + ": "
					+ e.getMessage());
			e.printStackTrace();
		}

		return user;
	}

	@SuppressWarnings("unchecked")
	public List<String> retrieveAccessTags(String username) {
		List<String> accessTags = new ArrayList<String>();

		if (username != null && !"".equals(username)) {
			try {
				// Configure the XMLRPC connection
				XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
				config.setServerURL(new URL(hdCatalogXmlRpcUrl));
				System.out.println("URL--->"+hdCatalogXmlRpcUrl+"--->"+hdCatalogXmlRpcAccessKey);
				XmlRpcClient client = new XmlRpcClient();
				client.setConfig(config);

				// Setup the parameters for the XMLRPC request
				Map<Object, Object> paramMap = new HashMap<Object, Object>();
				paramMap.put("lcaccesskey", hdCatalogXmlRpcAccessKey);
				paramMap.put("username", username);
				Object[] params = new Object[] { paramMap };

				// Make the request. Cast return value to its successful Object
				// type
				Object returnValue = client.execute("lcaccess.useraccesstags",
						params);
				Map<String, String> rawAccessMap = (Map<String, String>) returnValue;

				// Convert the raw Authorization values into local Access Tags
				accessTags = convertRawAccessTags(new ArrayList<String>(
						rawAccessMap.values()));
			} catch (Exception e) {
				System.err
						.println("Error trying to retrieve user permissions for "
								+ username + ": " + e.getMessage());
				e.printStackTrace();
			}
		}

		return accessTags;
	}

	private List<String> convertRawAccessTags(List<String> permissions) {
		List<String> accessTags = new ArrayList<String>();

		for (String permission : permissions) {
			List<String> newAccessTags = accessTagValueMap.get(permission);
			if (newAccessTags != null) {
				for (String newAccessTag : newAccessTags) {
					if (!accessTags.contains(newAccessTag)) {
						accessTags.add(newAccessTag);
					}
				}
			}
		}

		return accessTags;
	}

	private boolean supplimentalRetrieveLc(LearningUser user) {
		boolean overrode = false;

		// Certification Program Levels
		String pdpLevel = "None";
		String pfpLevel = "None";
		Map<String, String> certLevels = getCertificationLevels(user
				.getUserId());
		pdpLevel = certLevels.get("pdp");
		pfpLevel = certLevels.get("pfp");
		if (!user.getPdpLevel().equals(pdpLevel)) {
			user.setPdpLevel(pdpLevel);
			overrode = true;
		}
		if (!user.getPfpLevel().equals(pfpLevel)) {
			user.setPfpLevel(pfpLevel);
			overrode = true;
		}

		// Department
		user.setDepartment(getDepartmentName(user.getUserId()));

		// Admin
		AdminAccessRights adminAccessRights = getAdminAccessRights(user
				.getUserId());
		user.setAdmin(adminAccessRights.isAdmin());
		user.setAdminAccessRights(adminAccessRights);

		return overrode;
	}

	

	private Map<String, String> getCertificationLevels(int userId) {
		Map<String, String> certLevels = new HashMap<String, String>();

		try {
			String sql = "select case when pdpcert.userid is not null then 'Certified' when pdptran.userid is not null then 'Precertified' else 'None' end pdplevel, case when pfpcert.userid is not null then 'Certified' when pfptran.userid is not null then 'Precertified' else 'None' end pfplevel from lc_user u inner join lc_currentcertyear ccyear on ccyear.year > 0 left join lc_usercert pdpcert on pdpcert.userid = u.userid and pdpcert.program = 'pdp' and pdpcert.year >= ccyear.year left join ( lc_todolisttranscript pdptran inner join lc_todolist pdptodo on pdptodo.todolistid = pdptran.todolistid and pdptodo.certprogram = 'pdp' ) on pdptran.userid = u.userid and pdptran.iscomplete = 0 and pdptodo.certthruyear >= ccyear.year left join lc_usercert pfpcert on pfpcert.userid = u.userid and pfpcert.program = 'pfp' and pfpcert.year >= ccyear.year left join ( lc_todolisttranscript pfptran inner join lc_todolist pfptodo on pfptodo.todolistid = pfptran.todolistid and pfptodo.certprogram = 'pfp' ) on pfptran.userid = u.userid and pfptran.iscomplete = 0 and pfptodo.certthruyear >= ccyear.year where u.userid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			certLevels = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<String, String>>() {
						public Map<String, String> handleResultSet(ResultSet rs)
								throws Exception {
							Map<String, String> certLevels = new HashMap<String, String>();
							String pdpLevel = "None";
							String pfpLevel = "None";
							if (rs.next()) {
								pdpLevel = rs.getString("pdplevel");
								pfpLevel = rs.getString("pfplevel");
							}
							certLevels.put("pdp", pdpLevel);
							certLevels.put("pfp", pfpLevel);
							return certLevels;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Certification Levels for "
					+ userId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return certLevels;
	}

	private String getDepartmentName(int userId) {
		String departmentName = "";

		try {
			String sql = "select departmentname from lc_department where userid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			departmentName = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<String>() {
						public String handleResultSet(ResultSet rs)
								throws Exception {
							String departmentName = "";
							if (rs.next()) {
								departmentName = rs.getString("departmentname");
							}
							return departmentName;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Department Name for user "
					+ userId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return departmentName;
	}

	
	private AdminAccessRights getAdminAccessRights(int userId) {
		AdminAccessRights adminAccessRights = new AdminAccessRights();

		try {
			String sql = "select admin, userdata, course, todo, ekp, direct, eventedit, eventsuper from lc_admin where userid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			adminAccessRights = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<AdminAccessRights>() {
						public AdminAccessRights handleResultSet(ResultSet rs)
								throws Exception {
							AdminAccessRights adminAccessRights = new AdminAccessRights();
							if (rs.next()) {
								adminAccessRights.setAdminAdmin(rs
										.getInt("admin") != 0);
								adminAccessRights.setUserAdmin(rs
										.getInt("userdata") != 0);
								adminAccessRights.setCourseAdmin(rs
										.getInt("course") != 0);
								adminAccessRights.setTodoAdmin(rs
										.getInt("todo") != 0);
								adminAccessRights
										.setEkpAdmin(rs.getInt("ekp") != 0);
								adminAccessRights.setDirectAdmin(rs
										.getInt("direct") != 0);
								adminAccessRights.setEventEditAdmin(rs
										.getInt("eventedit") != 0);
								adminAccessRights.setEventSuperAdmin(rs
										.getInt("eventsuper") != 0);
							}
							return adminAccessRights;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving Admin User Access rights for user "
							+ userId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return adminAccessRights;
	}

	
	private List<LearningUser> resultSetToUserList(ResultSet rs)
			throws Exception {
		List<LearningUser> users = new ArrayList<LearningUser>();

		while (rs.next()) {
			LearningUser user = new LearningUser();
			// Active?
			user.setActive("Active".equalsIgnoreCase(rs.getString("status")));
			user.setAuthDescription(rs.getString("status"));

			// Basic User Info
			user.setUserId(rs.getInt("iIndividualId"));
			user.setUsername(rs.getString("vchEmailAddress"));
			user.setEmailAddress(rs.getString("vchEmailAddress"));
			user.setFirstName(rs.getString("vchFirstName"));
			user.setLastName(rs.getString("vchLastName"));

			// Address/Location Info
			user.setAddress1(rs.getString("vchAddress1"));
			user.setAddress2("");
			user.setCity(rs.getString("vchCity"));
			user.setRegion(rs.getString("chRegionCode"));
			user.setPostCode(rs.getString("vchPostCode"));
			user.setPhone(rs.getString("vchPhoneNumber"));
			user.setCellPhone(rs.getString("cellphone"));
			user.setCountry(rs.getString("chCountryCode"));

			// Basic Company Info
			user.setCompanyId(rs.getInt("iCompanyId"));
			user.setCompanyName(rs.getString("vchCompanyName"));
			String compType = rs.getString("compType");
			user.setCompanyType(compType);
			// TODO
			user.setDepartment("");

			if ("Dealer".equalsIgnoreCase(compType)) {
				// Dealer Specific Info
				user.setAllianceLevel(rs.getString("compSubType"));
				user.setAllianceCommitted(rs.getString("allianceCommitted"));
				user.setSponsoringFabricatorId(rs.getInt("fabricatorId"));
				user
						.setSponsoringFabricatorName(rs
								.getString("fabricatorName"));
				user.setSponsoringFabType(rs.getString("fabricatorType"));
				user.setSalesRepId(rs.getInt("salesRepId"));
				user.setSalesRepName(rs.getString("salesRepName"));
				user.setCenturion(!"".equals(rs.getString("centurion")));

				// Fab Specific Info
				user.setCompanyFabType("");
			} else if ("Fabricator".equalsIgnoreCase(compType)) {
				// Dealer Specific Info
				user.setAllianceLevel("");
				user.setAllianceCommitted("");
				user.setSponsoringFabricatorId(0);
				user.setSponsoringFabType("");
				user.setSalesRepId(0);
				user.setSalesRepName("");
				user.setCenturion(false);

				// Fab Specific Info
				user.setCompanyFabType(rs.getString("compSubType"));
			} else {
				// Dealer Specific Info
				user.setAllianceLevel("");
				user.setAllianceCommitted("");
				user.setSponsoringFabricatorId(0);
				user.setSponsoringFabType("");
				user.setSalesRepId(0);
				user.setSalesRepName("");
				user.setCenturion(false);

				// Fab Specific Info
				user.setCompanyFabType("");
			}

			// Training Program Levels
			user.setPdpLevel(onyxLCFieldMapper.onyxToEkpLookup("DealerProgram",
					rs.getString("dealerProg")));
			user.setPfpLevel(onyxLCFieldMapper.onyxToEkpLookup("FabProgram", rs
					.getString("fabProg")));
			user.setPipLevel(onyxLCFieldMapper.onyxToEkpLookup(
					"InstallerProgram", rs.getString("instProg")));

			// CDI & Franchises
			user.setCdi("Yes".equalsIgnoreCase(rs.getString("cdi")));
			user.setFranchise(rs.getString("franchise"));

			// Certified Motorization Specialist
			user.setCmsTrainingReady(rs.getInt("cmstrainingready") > 0);

			// Trade Alliance
			user.setTradeAlliance(rs.getInt("tradealliance") > 0);

			// Service Center
			user.setServiceCenter(rs.getInt("servicecenter") > 0);

			// Manager
			user.setManager(rs.getInt("ismanagercontact") > 0);

			// Admin
			user.setAdmin(false);

			users.add(user);
		}

		return users;
	}

	private void sync(LearningUser user) {
		if (userExists(user.getUserId())) {
			update(user);
		} else {
			insert(user);
		}
		if (!user.isActive()) {
			deleteAttempt(user.getUserId());
		}
	}

	private void upstreamSync(LearningUser user) {
		updateDealerProgram(user.getUserId(), user.getPdpLevel());
		updateFabProgram(user.getUserId(), user.getPfpLevel());
	}

	private void update(LearningUser user) {
		try {
			String sql = "update lc_user set username = ?, emailaddress = ?, firstname = ?, lastname = ?, address1 = ?, address2 = ?, city = ?, region = ?, postcode = ?, phone = ?, country = ?, companyid = ?, companyname = ?, companytype = ?, department = ?, alliancelevel = ?, alliancecommitted = ?, sponsoringfabricatorid = ?, sponsoringfabricatorname = ?, sponsoringfabtype = ?, salesrepid = ?, salesrepname = ?, centurion = ?, companyfabtype = ?, pdplevel = ?, piplevel = ?, pfplevel = ?, cdi = ?, franchise = ?, manager = ?, active = ?, cmstrainingready = ?, tradealliance = ?, servicecenter = ? where userid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(user.getUsername());
			params.add(user.getEmailAddress());
			params.add(user.getFirstName());
			params.add(user.getLastName());
			params.add(user.getAddress1());
			params.add(user.getAddress2());
			params.add(user.getCity());
			params.add(user.getRegion());
			params.add(user.getPostCode());
			params.add(user.getPhone());
			params.add(user.getCountry());
			params.add(user.getCompanyId());
			params.add(user.getCompanyName());
			params.add(user.getCompanyType());
			params.add(user.getDepartment());
			params.add(user.getAllianceLevel());
			params.add(user.getAllianceCommitted());
			params.add(user.getSponsoringFabricatorId());
			params.add(user.getSponsoringFabricatorName());
			params.add(user.getSponsoringFabType());
			params.add(user.getSalesRepId());
			params.add(user.getSalesRepName());
			params.add(user.isCenturion() ? 1 : 0);
			params.add(user.getCompanyFabType());
			params.add(user.getPdpLevel());
			params.add(user.getPipLevel());
			params.add(user.getPfpLevel());
			params.add(user.isCdi() ? 1 : 0);
			params.add(user.getFranchise());
			params.add(user.isManager() ? 1 : 0);
			params.add(user.isActive() ? 1 : 0);
			params.add(user.isCmsTrainingReady() ? 1 : 0);
			params.add(user.isTradeAlliance() ? 1 : 0);
			params.add(user.isServiceCenter() ? 1 : 0);
			params.add(user.getUserId());
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err.println("Error updating user " + user.getUsername()
					+ " in LC Database: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void insert(LearningUser user) {
		try {
			String sql = "insert into lc_user (userid, username, emailaddress, firstname, lastname, address1, address2, city, region, postcode, phone, country, companyid, companyname, companytype, department, alliancelevel, alliancecommitted, sponsoringfabricatorid, sponsoringfabricatorname, sponsoringfabtype, salesrepid, salesrepname, centurion, companyfabtype, pdplevel, piplevel, pfplevel, cdi, franchise, manager, active, cmstrainingready, tradealliance, servicecenter) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			List<Object> params = new ArrayList<Object>();
			params.add(user.getUserId());
			params.add(user.getUsername());
			params.add(user.getEmailAddress());
			params.add(user.getFirstName());
			params.add(user.getLastName());
			params.add(user.getAddress1());
			params.add(user.getAddress2());
			params.add(user.getCity());
			params.add(user.getRegion());
			params.add(user.getPostCode());
			params.add(user.getPhone());
			params.add(user.getCountry());
			params.add(user.getCompanyId());
			params.add(user.getCompanyName());
			params.add(user.getCompanyType());
			params.add(user.getDepartment());
			params.add(user.getAllianceLevel());
			params.add(user.getAllianceCommitted());
			params.add(user.getSponsoringFabricatorId());
			params.add(user.getSponsoringFabricatorName());
			params.add(user.getSponsoringFabType());
			params.add(user.getSalesRepId());
			params.add(user.getSalesRepName());
			params.add(user.isCenturion() ? 1 : 0);
			params.add(user.getCompanyFabType());
			params.add(user.getPdpLevel());
			params.add(user.getPipLevel());
			params.add(user.getPfpLevel());
			params.add(user.isCdi() ? 1 : 0);
			params.add(user.getFranchise());
			params.add(user.isManager() ? 1 : 0);
			params.add(user.isActive() ? 1 : 0);
			params.add(user.isCmsTrainingReady() ? 1 : 0);
			params.add(user.isTradeAlliance() ? 1 : 0);
			params.add(user.isServiceCenter() ? 1 : 0);
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err.println("Error inserting user " + user.getUsername()
					+ " into LC Database: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void deleteAttempt(int userId) {
		try {
			String sql = "delete from lc_user u where u.active = 0 and not exists (select userid from lc_history where userid = u.userid) and not exists (select userid from lc_usercert where userid = u.userid) and not exists (select userid from lc_todolisttranscript where userid = u.userid) and u.userid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err.println("Error attempting to delete user " + userId
					+ ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	private boolean userExists(int userId) {
		boolean exists = false;
		try {
			String sql = "select userid from lc_user where userid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			exists = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Boolean>() {
						public Boolean handleResultSet(ResultSet rs)
								throws Exception {
							return new Boolean(rs.next());
						}
					}).booleanValue();
		} catch (Exception e) {
			System.err.println("Error seeing if user " + userId
					+ " exists in LC Database: " + e.getMessage());
			e.printStackTrace();
		}
		return exists;
	}

	public int retrieveUserIdByUserName(String username) {
		int id = 0;

		try {
			String sql = "SELECT iIndividualId AS id FROM Individual (nolock) WHERE vchEmailAddress = ? AND tiRecordStatus = 1";
			List<Object> params = new ArrayList<Object>();
			params.add(username);
			id = DataUtils.query(sql, params, ONYX_PROD_DATA_SOURCE,
					new ResultSetHandler<Integer>() {
						public Integer handleResultSet(ResultSet rs)
								throws Exception {
							if (rs.next()) {
								int id = rs.getInt("id");
								return new Integer(id);
							} else {
								return new Integer(0);
							}
						}
					}).intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}

	
	public LearningUser retrieveSyncByUsername(String username) {
		int id = retrieveUserIdByUserName(username);

		if (id > 0) {
			return retrieveSync(id);
		}

		return null;
	}
	
	private void updateDealerProgram(int userId, String value) {
		if (writeToOnyx) {
			String onyxValue = onyxLCFieldMapper.ekpToOnyxLookup(
					"DealerProgram", value);
			try {
				String sql = "EXEC compass.updateIndividualDealerProgram @iIndividualId = ?, @dealerProgram = ?";
				//String sql = "declare @dtActionDate datetime set @dtActionDate = getdate() exec compass.updateIndividualDealerProgram @IndividualId = ?, @dealerProgram = ?, @ActionDate = @dtActionDate";
				List<Object> params = new ArrayList<Object>();
				params.add(userId);
				params.add(onyxValue);
				DataUtils.execute(sql, params, ONYX_PROD_DATA_SOURCE);
			} catch (Exception e) {
				System.err
						.println("Error updating Dealer Program value to "
								+ onyxValue + " for user " + userId + ": "
								+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void updateFabProgram(int userId, String value) {
		if (writeToOnyx) {
			String onyxValue = onyxLCFieldMapper.ekpToOnyxLookup("FabProgram",
					value);
			try {
				String sql = "EXEC compass.updateIndividualFabricatorProgram @iIndividualId = ?, @fabricatorProgram = ?";
				//String sql = "declare @dtActionDate datetime set @dtActionDate = getdate() exec compass.updateIndividualFabricatorProgram @IndividualId = ?, @fabricatorProgram = ?, @ActionDate = @dtActionDate";
				List<Object> params = new ArrayList<Object>();
				params.add(userId);
				params.add(onyxValue);
				DataUtils.execute(sql, params, ONYX_PROD_DATA_SOURCE);
			} catch (Exception e) {
				System.err
						.println("Error updating Fab Program value to " + onyxValue
								+ " for user " + userId + ": " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public void updateInstallerProgram(int userId, String value) {
		if (writeToOnyx) {
			String onyxValue = onyxLCFieldMapper.ekpToOnyxLookup(
					"InstallerProgram", value);
			try {
				//String sql = "EXEC compass.updateIndividualInstallerProgram @iIndividualId = ?, @installerProgram = ?";
				String sql = "declare @dtActionDate datetime set @dtActionDate = getdate() exec compass.updateIndividualInstallerProgram @IndividualId = ?, @InstallerProgram = ?, @ActionDate = @dtActionDate";
				List<Object> params = new ArrayList<Object>();
				params.add(userId);
				params.add(onyxValue);
				DataUtils.execute(sql, params, ONYX_PROD_DATA_SOURCE);
			} catch (Exception e) {
				System.err
						.println("Error updating Installer Program value to "
								+ onyxValue + " for user " + userId + ": "
								+ e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public void saveDepartment(int userId, String departmentName) {
		try {
			String sql = "delete from lc_department where userid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err.println("Error updating department for user " + userId
					+ ": " + e.getMessage());
			e.printStackTrace();
		}

		try {
			String sql = "insert into lc_department (userid, departmentname) values (?, ?)";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			params.add(departmentName);
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err.println("Error updating department for user " + userId
					+ ": " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String retrieveInstallerProgram(int userId) {
		String installerProgram = "";
		try {
			String sql = "SELECT tlc.installerProgram AS instProg FROM compass.TheLearningCenterUserView tlc (nolock) WHERE tlc.iIndividualId = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			installerProgram = DataUtils.query(sql, params, ONYX_PROD_DATA_SOURCE,
					new ResultSetHandler<String>() {
						public String handleResultSet(ResultSet rs)
								throws Exception {
							String installerProgram = null;
							if (rs.next()) {
								installerProgram = rs.getString("instProg");
							} 
							return onyxLCFieldMapper.onyxToEkpLookup(
									"InstallerProgram", installerProgram);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return installerProgram;
	}
}
	
	

	

	
	

	
	