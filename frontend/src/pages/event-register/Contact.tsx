import React, { useEffect, useState } from "react";
import { postJSON } from "@/lib/api";

export default function Contact() {
  const [rid, setRid] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
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
      `/api/event-register/contact/${rid}`,
      { first_name: firstName, last_name: lastName, email }
    );
    setMsg(`${data.step} saved for ${data.registration_id}`);
  }

  return (
    <form onSubmit={submit} className="space-y-3">
      <input className="border rounded px-3 py-2 w-full" placeholder="Registration ID" value={rid} onChange={(e) => setRid(e.target.value)} />
      <input className="border rounded px-3 py-2 w-full" placeholder="First name" value={firstName} onChange={(e) => setFirstName(e.target.value)} />
      <input className="border rounded px-3 py-2 w-full" placeholder="Last name" value={lastName} onChange={(e) => setLastName(e.target.value)} />
      <input className="border rounded px-3 py-2 w-full" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
      <button className="bg-blue-600 text-white rounded px-4 py-2">Save</button>
      {msg && <div className="text-sm">{msg}</div>}
    </form>
  );
}
