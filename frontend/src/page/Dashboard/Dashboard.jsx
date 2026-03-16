import "./Dashboard.scss";
import NavBar from "../../components/NavBar/NavBar";
import TraderList from "./components/TraderList";
import { useState } from "react";
import AddTraderButton from "./components/AddTraderButton.jsx";

export default function Dashboard() {
  const [refreshKey, setRefreshKey] = useState(0);

  const triggerRefresh = () => setRefreshKey((k) => k + 1);

  return (
      <div className="dashboard">
        <NavBar />

        <div className="dashboard__content">
          <h2>Dashboard</h2>

          <AddTraderButton onTraderAdded={triggerRefresh} />

          <TraderList refreshKey={refreshKey} />
        </div>
      </div>
  );
}
