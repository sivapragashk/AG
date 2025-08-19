import React, { useState } from "react";
import { postJSON } from "@/lib/api";

export default function Options() {
  const [rid, setRid] = useState("");
  const [sessionId, setSessionId] = useState("");
  const [agree, setAgree] = useState(false);
  const [msg, setMsg] = useState<string | null>(null);

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setMsg(null);
    const data = await postJSON<{ registration_id: string; step: string; ok: boolean }>(
      `/api/event-register/options/${rid}`,
      { session_id: sessionId, agree_terms: agree }
    );
    setMsg(`${data.step} saved for ${data.registration_id}`);
  }

  return (
    <form onSubmit={submit} className="space-y-3">
      <input className="border rounded px-3 py-2 w-full" placeholder="Registration ID" value={rid} onChange={(e) => setRid(e.target.value)} />
      <input className="border rounded px-3 py-2 w-full" placeholder="Session ID" value={sessionId} onChange={(e) => setSessionId(e.target.value)} />
      <label className="flex items-center gap-2">
        <input type="checkbox" checked={agree} onChange={(e) => setAgree(e.target.checked)} /> Agree
      </label>
      <button className="bg-blue-600 text-white rounded px-4 py-2">Save</button>
      {msg && <div className="text-sm">{msg}</div>}
    </form>
  );
}
