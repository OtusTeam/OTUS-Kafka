package ru.otus.sparkmlstreaming

case class Data(
    CLIENTNUM: Int,
    Attrition_Flag: String,
    Customer_Age: Int,
    Gender: String,
    Dependent_count: Int,
    Education_Level: String,
    Marital_Status: String,
    Income_Category: String,
    Card_Category: String,
    Months_on_book: Int,
    Total_Relationship_Count: Int,
    Months_Inactive_12_mon: Int,
    Contacts_Count_12_mon: Int,
    Credit_Limit: Double,
    Total_Revolving_Bal: Int,
    Avg_Open_To_Buy: Double,
    Total_Amt_Chng_Q4_Q1: Double,
    Total_Trans_Amt: Int,
    Total_Trans_Ct: Int,
    Total_Ct_Chng_Q4_Q1: Double,
    Avg_Utilization_Ratio: Double,
    Naive_Bayes_Classifier_Attrition_Flag_Card_Category_Contacts_Count_12_mon_Dependent_count_Education_Level_Months_Inactive_12_mon_1: Double,
    Naive_Bayes_Classifier_Attrition_Flag_Card_Category_Contacts_Count_12_mon_Dependent_count_Education_Level_Months_Inactive_12_mon_2: Double
)

object Data {
  def apply(a: Array[String]): Data =
    Data(
      a(0).toInt,
      a(1),
      a(2).toInt,
      a(3),
      a(4).toInt,
      a(5),
      a(6),
      a(7),
      a(8),
      a(9).toInt,
      a(10).toInt,
      a(11).toInt,
      a(12).toInt,
      a(13).toDouble,
      a(14).toInt,
      a(15).toDouble,
      a(16).toDouble,
      a(17).toInt,
      a(18).toInt,
      a(19).toDouble,
      a(20).toDouble,
      a(21).toDouble,
      a(22).toDouble
    )
}
