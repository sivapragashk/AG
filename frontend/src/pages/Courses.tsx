import React, { useEffect, useState } from "react";
import { getJSON } from "@/lib/api";

type Course = { id: string; title: string };

const Courses: React.FC = () => {
  const [items, setItems] = useState<Course[]>([]);

  useEffect(() => {
    getJSON<Course[]>("/api/courses").then(setItems).catch(() => setItems([]));
  }, []);

  return (
    <div className="p-4 space-y-2">
      <h1 className="text-xl font-semibold">Courses</h1>
      <ul className="list-disc pl-6">
        {items.map((c) => (
          <li key={c.id}>
            {c.id} - {c.title}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Courses;
