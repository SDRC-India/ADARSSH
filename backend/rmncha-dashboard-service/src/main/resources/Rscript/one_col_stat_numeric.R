# f1 <- function()
# {
#   write.csv(dt1, file = "c:/out/x1.csv",row.names=FALSE)
# }



set_col <- function()
{
  dt<-read.csv("c:/R_Input/RMNCH-A_Analysis.csv",header = T,na.strings="")
  # dt1=dt
  # write.csv(dt1, "c:/out/dt213211.csv",row.names=TRUE)
  return(names(dt))
  
}


begin1 <- function(x,fileName)
{
 # dt1=dt
  #write.csv(dt1, "c:/out/dt213211.csv",row.names=TRUE)
 dt<-read.csv(fileName,header = T,na.strings="")

dt1<-dt
dt1<-dt1[, colSums( is.na(dt1) ) < nrow(dt1)]
dt1<-dt1[rowSums(is.na(dt1)) != ncol(dt1),]
num_var<-sapply(dt1,is.numeric)
cat_var<-!sapply(dt1,is.numeric)
col_num<-c(names(dt1[num_var]))
col_cat<-c(names(dt1[cat_var]))
#===================== file name start ===============
Time=Sys.time()
dd = format(as.POSIXct(Time,format="%d/%m/%Y"),"%d")
mm = format(as.POSIXct(Time,format="%d/%m/%Y"),"%m")
yy = format(as.POSIXct(Time,format="%d/%m/%Y"),"%Y")
hour = format(as.POSIXct(Time,format="%H:%M:%S"),"%H")
minute = format(as.POSIXct(Time,format="%H:%M:%S"),"%M")
sec = format(as.POSIXct(Time,format="%H:%M:%S"),"%S")
a1=paste(dd,mm,yy,hour,minute,sec,sep="_")


#a1=as.character(Sys.time())
nm <- paste("c:/out/one_col_stat_numeric_",a1,".csv",sep="")
#===================== file name end============
# aa()

# arg="3"
# f1
# dt<-read.csv("c:/R_Input/RMNCH-A_Analysis.csv",header = T,na.strings="")
# mystats_numeric_one_col(arg)
#-------------------------------------------------------------------
x<-as.numeric(x)
x<-dt1[,c(x)]
if (class(x)=="character"||class(x)=="factor")
{
  return(0)
}
else{
  nmiss<-sum(is.na(x))
  a<-x[!is.na(x)]
  m<-mean(a)
  min<-min(a)
  max<-max(a)
  s<-sd(a)
  v<-var(a)
  p1<-quantile(a,0.01)
  p2<-quantile(a,0.05)
  p10<-quantile(a,0.10)
  q1<-quantile(a,0.25)
  q2<-quantile(a,0.50)
  q3<-quantile(a,0.75)
  p90<-quantile(a,0.90)
  p95<-quantile(a,0.95)
  p99<-quantile(a,0.99)
  UC<-round(m+3*s,0)
  LC<-round(m-3*s,0)
  outliers<-sum(!is.na(x[x>UC | x<LC]))
  # return(y=data.frame(c(Number_of_missing_values=nmiss,Standard_deviation=s,Variance=v,Number_of_outliers=outliers,
  #                       Minimum=min,Maximum=max,Mean=m,Tenth_percentile=p10,First_quartile=q1,Second_quartile=q2,
  #                       Third_quartile=q3,Ninetieth_percentile=p90,Ninety_ninth_percentile=p99)))
  y1 <- data.frame(c(Number_of_missing_values=nmiss,Standard_deviation=s,Variance=v,Number_of_outliers=outliers,
                     Minimum=min,Maximum=max,Mean=m,Tenth_percentile=p10,First_quartile=q1,Second_quartile=q2,
                     Third_quartile=q3,Ninetieth_percentile=p90,Ninety_ninth_percentile=p99))
  if(y1==0)
  {
    # print("Please enter a Numeric variable")
    return ("Please enter a Numeric variable")
  }else
  {
    y1<-as.data.frame(y1)
    names(y1)<-names(dt1[as.numeric(arg)])
    # View(y1)
    write.csv(y1, "c:/out/one_col_stat_numeric.csv",row.names=TRUE)
    return("ok")
  }
}
}
#-----------------------------------------------------------------------


# class(dt1[32])
# if(class(y1)=="numeric")
# {
#   print("Please enter a numeric column")
# }else
# {
#   names(y1)<-names(dt1[as.numeric(arg)])
#   # View(y1)
#   write.csv(y1, file = "c:/out/one_stat_numeric.csv",row.names=TRUE)
# }
# aa=function()
# {
#   print("ram")
# }

