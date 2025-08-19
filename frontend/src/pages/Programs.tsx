import React, { useEffect, useState } from "react";
import { getJSON } from "@/lib/api";

type Program = { id: string; name: string };

const Programs: React.FC = () => {
  const [items, setItems] = useState<Program[]>([]);

  useEffect(() => {
    getJSON<Program[]>("/api/programs").then(setItems).catch(() => setItems([]));
  }, []);

  return (
    <div className="p-4 space-y-2">
      <h1 className="text-xl font-semibold">Programs</h1>
      <ul className="list-disc pl-6">
        {items.map((p) => (
          <li key={p.id}>
            {p.id} - {p.name}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Programs;
