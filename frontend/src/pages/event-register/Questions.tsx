import React, { useEffect, useState } from "react";
import { postJSON } from "@/lib/api";

export default function Questions() {
  const [rid, setRid] = useState("");
  const [q1, setQ1] = useState("");
  const [q2, setQ2] = useState("");
  const [msg, setMsg] = useState<string | null>(null);

  useEffect(() => {
    if (!rid) {
      try {
        const v = localStorage.getItem("rid") || "";
        if (v) setRid(v);
      } catch {}
    }
  }, [rid]);

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setMsg(null);
    const data = await postJSON<{ registration_id: string; step: string; ok: boolean }>(
      `/api/event-register/questions/${rid}`,
      { q1, q2 }
    );
    setMsg(`${data.step} saved for ${data.registration_id}`);
  }

  return (
    <form onSubmit={submit} className="space-y-3">
      <input className="border rounded px-3 py-2 w-full" placeholder="Registration ID" value={rid} onChange={(e) => setRid(e.target.value)} />
      <input className="border rounded px-3 py-2 w-full" placeholder="Q1" value={q1} onChange={(e) => setQ1(e.target.value)} />
      <input className="border rounded px-3 py-2 w-full" placeholder="Q2" value={q2} onChange={(e) => setQ2(e.target.value)} />
      <button className="bg-blue-600 text-white rounded px-4 py-2">Save</button>
      {msg && <div className="text-sm">{msg}</div>}
    </form>
  );
}
