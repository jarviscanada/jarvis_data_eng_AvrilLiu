import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import Dashboard from "./page/Dashboard/Dashboard";
import TraderDetails from "./page/TraderDetails";
import QuotePage from "./page/QuotePage/QuotePage";

export default function Router() {
  return (
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Navigate to="/dashboard"/>}/>
          <Route path="/dashboard" element={<Dashboard/>}/>
          <Route path="/traders/:id" element={<TraderDetails/>}/>
          <Route path="/traders" element={<Dashboard />} />
          <Route path="/quotes" element={<QuotePage />} />
        </Routes>
      </BrowserRouter>
  );
}
