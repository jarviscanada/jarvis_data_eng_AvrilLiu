import { useParams } from "react-router-dom";

export default function TraderDetails() {
  const { id } = useParams();

  return (
      <div style={{ padding: 16 }}>
        <h2>Trader Details</h2>
        <p>Trader ID: {id}</p>
      </div>
  );
}
