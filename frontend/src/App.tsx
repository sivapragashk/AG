import React from "react";
import { Routes, Route, Link } from "react-router-dom";
import Company from "@/pages/event-register/Company";
import Contact from "@/pages/event-register/Contact";
import Options from "@/pages/event-register/Options";
import User from "@/pages/event-register/User";
import Questions from "@/pages/event-register/Questions";
import Payment from "@/pages/event-register/Payment";
import Confirm from "@/pages/event-register/Confirm";
import Courses from "@/pages/Courses";
import Todos from "@/pages/Todos";
import Programs from "@/pages/Programs";

export default function App() {
  return (
    <div className="min-h-screen bg-gray-50 text-gray-900">
      <header className="border-b bg-white">
        <div className="max-w-4xl mx-auto p-4 flex items-center justify-between">
          <Link to="/" className="font-semibold">AG</Link>
          <nav className="space-x-4 text-sm">
            <Link to="/event-register/company">Company</Link>
            <Link to="/event-register/contact">Contact</Link>
            <Link to="/event-register/options">Options</Link>
            <Link to="/event-register/user">User</Link>
            <Link to="/event-register/questions">Questions</Link>
            <Link to="/event-register/payment">Payment</Link>
            <Link to="/event-register/confirm">Confirm</Link>
            <span className="text-gray-300">|</span>
            <Link to="/courses">Courses</Link>
            <Link to="/todos">ToDos</Link>
            <Link to="/programs">Programs</Link>
          </nav>
        </div>
      </header>
      <main className="max-w-4xl mx-auto p-4">
        <Routes>
          <Route path="/" element={<div>Home</div>} />
          <Route path="/event-register/company" element={<Company />} />
          <Route path="/event-register/contact" element={<Contact />} />
          <Route path="/event-register/options" element={<Options />} />
          <Route path="/event-register/user" element={<User />} />
          <Route path="/event-register/questions" element={<Questions />} />
          <Route path="/event-register/payment" element={<Payment />} />
          <Route path="/event-register/confirm" element={<Confirm />} />
          <Route path="/courses" element={<Courses />} />
          <Route path="/todos" element={<Todos />} />
          <Route path="/programs" element={<Programs />} />
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
