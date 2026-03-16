# Retail Customer Analytics with RFM Segmentation

This project analyzes an e-commerce retail dataset to understand customer purchasing behavior and identify high-value customer groups using **RFM (Recency, Frequency, Monetary) analysis**.

The workflow combines **SQL data exploration** with **Python-based data wrangling and analytics** to transform raw transactional data into meaningful customer insights.

---

## Project Structure

```
python_data_analytics
│
├── assets
│   └── images
│       ├── project_workflow.png
│       └── rfm_segmentation_grid.png
│
├── psql
│   ├── retail.sql
│   └── data_explore.sql
│
├── python_data_wrangling
│   ├── retail_data_analytics_wrangling.ipynb
│   └── rfm_sample.ipynb
│
├── README.md
├── requirements.txt
└── .gitignore
```

**assets/images**  
Contains the architecture of this project.

**psql**  
Contains SQL scripts used to load the dataset and perform initial exploration.

**python_data_wrangling**  
Contains Jupyter notebooks used for data cleaning, analytics, and customer segmentation.

---

## Analytics Workflow

```
Raw Retail Dataset
        ↓
Data Preparation
(rename columns, type conversion, cleaning)
        ↓
Exploratory Data Analysis
(invoice distribution, monthly sales, growth trends)
        ↓
Customer Aggregation
(Recency, Frequency, Monetary calculation)
        ↓
RFM Scoring
(quantile-based scoring from 1 to 5)
        ↓
Customer Segmentation
(Champions, Loyal Customers, Hibernating, etc.)
        ↓
Business Insights
(customer retention and marketing strategies)
```

---

## Dataset

The analysis uses the **UK Online Retail dataset**, which contains transactional records from an online retail store.

Each row represents a product item within an invoice. Since a single invoice may contain multiple items, order-level and customer-level metrics must be aggregated from the raw transaction data.

Key columns include:

- Invoice – unique order identifier
- StockCode – product identifier
- Description – product description
- Quantity – number of items purchased
- InvoiceDate – transaction timestamp
- Price – unit price of the item
- Customer ID – customer identifier
- Country – customer location

---

## Data Preparation

Because the CSV dataset does not contain standardized naming conventions or explicit data types, several preprocessing steps were required:

- Renaming columns to consistent naming conventions
- Converting transaction timestamps to datetime format
- Creating a **TotalPrice** field (Quantity × Price)
- Handling missing values for customer identifiers

These steps ensure the dataset can be used for reliable analytics.

---

## RFM Segmentation

Customer segmentation was performed using the **RFM framework**, which evaluates customers based on three behavioral metrics:

- **Recency** – how recently a customer made a purchase
- **Frequency** – how often a customer purchases
- **Monetary** – how much money a customer spends

Each metric was converted into a score from **1 to 5** using quantile-based binning. Customers were then grouped into behavioral segments using combinations of Recency and Frequency scores.

Example segments include:

- Champions
- Loyal Customers
- Potential Loyalists
- At Risk
- Can't Lose
- Hibernating

---

## Key Insights

Three segments were selected for closer evaluation.

**Champions**

- Customers: 852
- Average Recency: 30 days
- Average Frequency: 19 purchases
- Average Monetary Value: £10,796

These customers are the most valuable group and contribute significantly to total revenue. Retention strategies such as loyalty programs, exclusive promotions, and personalized recommendations should be prioritized for this segment.

**Can't Lose**

- Customers: 71
- Average Recency: 353 days
- Average Frequency: 16 purchases
- Average Monetary Value: £8,356

These customers were historically high-value customers but have not made purchases recently. Targeted win-back campaigns and personalized offers may help re-engage them.

**Hibernating**

- Customers: 1522
- Average Recency: 481 days
- Average Frequency: 1 purchase
- Average Monetary Value: £438

These customers show very low engagement. Low-cost reactivation campaigns may bring some of them back, but large marketing investments may not be justified.

---

## Quick Start

### 1. Start PostgreSQL using Docker

If the PostgreSQL container has not been created yet, run:

```
./psql/psql_docker.sh create postgres password
```

Start the PostgreSQL instance:

```
./psql/psql_docker.sh start
```

---

### 2. Create Database Tables

Load the dataset schema and data into PostgreSQL:

```
psql -h localhost -U postgres -d lgs_db -f psql/retail.sql
```

Enter the password when prompted.

---

### 3. Set Up the Python Environment

Create and activate a virtual environment.

Mac/Linux:

```
python3 -m venv venv
source venv/bin/activate
```

Windows:

```
python -m venv venv
venv\Scripts\activate
```

Install required dependencies:

```
pip install -r requirements.txt
```

---

### 4. Run the Notebook

Open the notebook:

```
python_data_wrangling/retail_data_analytics_wrangling.ipynb
```

You can run the notebook either using **VS Code** or **Jupyter Notebook**.

VS Code:

1. Open the notebook
2. Select the virtual environment as the kernel
3. Click **Run All**

Command Line:

```
jupyter notebook python_data_wrangling/retail_data_analytics_wrangling.ipynb
```

Then select **Kernel → Restart & Run All**.

---

## References

RFM Segmentation Guide  
https://docs.exponea.com/docs/rfm-segmentation

Recency-Frequency Grid  
https://clevertap.com/blog/automate-user-segmentation-with-rfm-analysis/