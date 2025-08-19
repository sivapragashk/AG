package com.hd.cedg.lms.servlet;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import com.hd.cedg.lms.model.ExecutionResult;

public abstract class FlowControlServlet<Serializable> extends BaseLmsControlServlet {

	private static final long serialVersionUID = -11014127451386610L;

	public static final String FORM_OBJECT_NAME = "formObject";

	@SuppressWarnings("unchecked")
	public ExecutionResult execute(String action, Map<String, Object> params) {
		ExecutionResult result = null;

		// Get the marshalled form object. Unmarshall it.
		Serializable formObject = null;
		String formObjectSerialized = (String) params.get(FORM_OBJECT_NAME);
		if (formObjectSerialized != null) {
			formObject = unmarshall(formObjectSerialized);
		}
		params.put(getFormObjectName(), formObject);

		String state = (String) params.get("flowState");
		// If we're missing action, start at the beginning
		if (action == null || state == null) {
			action = "start";
			state = "start";
			params.put("controllerAction", action);
			params.put("flowState", state);
		}

		// Execute the flow
		result = executeFlow(action, state, params);

		// If the attributes have a new copy of the form object, use it
		if (result.getAttributes().get(getFormObjectName()) != null) {
			formObject = (Serializable) result.getAttributes().get(
					getFormObjectName());
		}
		// If the parameters still have a copy of the form object, use it
		else if (params.get(getFormObjectName()) != null) {
			formObject = (Serializable) params.get(getFormObjectName());
		}
		// Marshall the form object & reinsert it into the attributes
		formObjectSerialized = marshall(formObject);
		result.addAttribute(FORM_OBJECT_NAME, formObjectSerialized);
		result.addAttribute(getFormObjectName(), formObject);

		return result;
	}

	private String marshall(Serializable formObject) {
		String marshalled = "";
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream objStream = new ObjectOutputStream(baos);
			objStream.writeObject(formObject);
//			String ser = baos.toString();
//			byte[] bytes = ser.getBytes();
			byte[] bytes = baos.toByteArray();
			for (byte b : bytes) {
				int n = b + 128;
				if (n < 10)
					marshalled += "00" + n;
				else if (n < 100)
					marshalled += "0" + n;
				else
					marshalled += n;
			}
		} catch (IOException e) {
			System.err.println("Error marshalling Form Object: " + e);
			e.printStackTrace();
		}
		return marshalled;
	}

	@SuppressWarnings("unchecked")
	private Serializable unmarshall(String formObjectSerialized) {
		Serializable formObject = null;
		try {
			byte[] nums = formObjectSerialized.getBytes();
			byte[] bytes = new byte[nums.length / 3];
			for (int i = 0; i < nums.length; i += 3) {
				int n = (100 * (nums[i] - 48) + 10 * (nums[i + 1] - 48) + (nums[i + 2] - 48)) - 128;
				bytes[i / 3] = (byte) n;
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream objstream = new ObjectInputStream(bais);
			formObject = (Serializable) objstream.readObject();
			objstream.close();
		} catch (IOException e) {
			System.err.println("Error unmarshalling Form Object: " + e);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Error unmarshalling Form Object: " + e);
			e.printStackTrace();
		}
		return formObject;
	}

	protected abstract ExecutionResult executeFlow(String action, String state,
			Map<String, Object> params);

	public abstract String getFormObjectName();

}
