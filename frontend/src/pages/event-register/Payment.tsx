import React, { useEffect, useState } from "react";
import { postJSON } from "@/lib/api";

export default function Payment() {
  const [rid, setRid] = useState("");
  const [amount, setAmount] = useState(100);
  const [card, setCard] = useState("");
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
      `/api/event-register/payment/${rid}`,
      { amount_cents: amount, currency: "USD", card_last4: card }
    );
    setMsg(`${data.step} saved for ${data.registration_id}`);
  }

  return (
    <form onSubmit={submit} className="space-y-3">
      <input className="border rounded px-3 py-2 w-full" placeholder="Registration ID" value={rid} onChange={(e) => setRid(e.target.value)} />
      <input className="border rounded px-3 py-2 w-full" placeholder="Amount (cents)" type="number" value={amount} onChange={(e) => setAmount(parseInt(e.target.value || "0", 10))} />
      <input className="border rounded px-3 py-2 w-full" placeholder="Card last4" value={card} onChange={(e) => setCard(e.target.value)} />
      <button className="bg-blue-600 text-white rounded px-4 py-2">Save</button>
      {msg && <div className="text-sm">{msg}</div>}
    </form>
  );
}
