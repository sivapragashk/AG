import React, { useState } from "react";
import { postJSON } from "@/lib/api";

const Company: React.FC = () => {
  const [companyName, setCompanyName] = useState("");
  const [response, setResponse] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    setResponse(null);
    try {
      const data = await postJSON<{ registration_id: string; step: string; ok: boolean }>(
        "/api/event-register/company",
        { company_name: companyName }
      );
      try {
        localStorage.setItem("rid", data.registration_id);
      } catch {}
      setResponse(`Registration ${data.registration_id}`);
    } catch (err: any) {
      setResponse(err.message || "Error");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="bg-white rounded-md shadow p-6">
      <h1 className="text-xl font-semibold mb-4">Event Registration - Company</h1>
      <form onSubmit={submit} className="space-y-4">
        <div>
          <label className="block text-sm mb-1">Company Name</label>
          <input
            className="border rounded px-3 py-2 w-full"
            value={companyName}
            onChange={(e) => setCompanyName(e.target.value)}
            placeholder="Enter company name"
          />
        </div>
        <button
          type="submit"
          disabled={loading || !companyName}
          className="bg-blue-600 text-white rounded px-4 py-2 disabled:opacity-50"
        >
          {loading ? "Submitting..." : "Continue"}
        </button>
      </form>
      {response && <div className="mt-4 text-sm">{response}</div>}
    </div>
  );
};

export default Company;
