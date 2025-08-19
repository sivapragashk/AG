import React, { useState } from "react";
import { postJSON } from "@/lib/api";

export default function User() {
  const [rid, setRid] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [msg, setMsg] = useState<string | null>(null);

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setMsg(null);
    const data = await postJSON<{ registration_id: string; step: string; ok: boolean }>(
      `/api/event-register/user/${rid}`,
      { username, password }
    );
    setMsg(`${data.step} saved for ${data.registration_id}`);
  }

  return (
    <form onSubmit={submit} className="space-y-3">
      <input className="border rounded px-3 py-2 w-full" placeholder="Registration ID" value={rid} onChange={(e) => setRid(e.target.value)} />
      <input className="border rounded px-3 py-2 w-full" placeholder="Username" value={username} onChange={(e) => setUsername(e.target.value)} />
      <input className="border rounded px-3 py-2 w-full" placeholder="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
      <button className="bg-blue-600 text-white rounded px-4 py-2">Save</button>
      {msg && <div className="text-sm">{msg}</div>}
    </form>
  );
}
