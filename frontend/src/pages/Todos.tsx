import React, { useEffect, useState } from "react";
import { getJSON, postJSON } from "@/lib/api";

type Todo = { id: string; title: string; completed: boolean };

const Todos: React.FC = () => {
  const [items, setItems] = useState<Todo[]>([]);

  useEffect(() => {
    getJSON<Todo[]>("/api/todos").then(setItems).catch(() => setItems([]));
  }, []);

  async function complete(id: string) {
    const t = await postJSON<Todo>(`/api/todos/${id}/complete`, {});
    setItems((prev) => prev.map((x) => (x.id === id ? t : x)));
  }

  return (
    <div className="p-4 space-y-2">
      <h1 className="text-xl font-semibold">ToDo List</h1>
      <ul className="space-y-2">
        {items.map((t) => (
          <li key={t.id} className="flex items-center gap-3">
            <span className={t.completed ? "line-through text-gray-500" : ""}>{t.title}</span>
            {!t.completed && (
              <button className="px-2 py-1 bg-blue-600 text-white rounded" onClick={() => complete(t.id)}>
                Complete
              </button>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Todos;
