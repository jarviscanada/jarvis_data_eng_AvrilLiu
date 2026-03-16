import { useEffect, useState } from "react";
import axios from "axios";
import { Table } from "antd";
import "antd/dist/antd.css";

import NavBar from "../../components/NavBar/NavBar";
import { quotesUrl } from "../../util/constants";

import "./QuotePage.scss";

export default function QuotePage() {
  const [quotes, setQuotes] = useState([]);

  const getQuotes = async () => {
    try {
      const response = await axios.get(quotesUrl);
      setQuotes(response.data || []);
    } catch (err) {
      console.error("Failed to load quotes", err);
    }
  };

  useEffect(() => {
    getQuotes();
  }, []);

  const columns = [
    { title: "Ticker", dataIndex: "ticker", key: "ticker" },
    { title: "Last Price", dataIndex: "lastPrice", key: "lastPrice" },
    { title: "Bid Price", dataIndex: "bidPrice", key: "bidPrice" },
    { title: "Bid Size", dataIndex: "bidSize", key: "bidSize" },
    { title: "Ask Price", dataIndex: "askPrice", key: "askPrice" },
    { title: "Ask Size", dataIndex: "askSize", key: "askSize" },
  ];

  return (
      <div className="quote-page">
        <NavBar />

        <div className="quote-page__content">
          <div className="title">Quotes</div>

          <Table
              rowKey="id"
              dataSource={quotes}
              columns={columns}
              pagination={false}
          />
        </div>
      </div>
  );
}
