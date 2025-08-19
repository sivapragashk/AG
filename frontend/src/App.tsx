import React from "react";
import { Routes, Route, Link } from "react-router-dom";
import Company from "./pages/event-register/Company";

export default function App() {
  return (
    <div className="min-h-screen bg-gray-50 text-gray-900">
      <header className="border-b bg-white">
        <div className="max-w-4xl mx-auto p-4 flex items-center justify-between">
          <Link to="/" className="font-semibold">AG</Link>
          <nav className="space-x-4">
            <Link to="/event-register/company" className="text-sm">Event Register</Link>
          </nav>
        </div>
      </header>
      <main className="max-w-4xl mx-auto p-4">
        <Routes>
          <Route path="/" element={<div>Home</div>} />
          <Route path="/event-register/company" element={<Company />} />
        </Routes>
      </main>
      <footer className="border-t bg-white">
        <div className="max-w-4xl mx-auto p-4 text-sm text-gray-500">
          © AG
        </div>
      </footer>
    </div>
  );
}
