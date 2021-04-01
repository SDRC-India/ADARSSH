
begin1 <- function(arg,fileName)
{

mystats_numeric<-function(x)
{
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
  return(c(Number_of_missing_values=nmiss,Standard_deviation=s,Variance=v,Number_of_outliers=outliers,
           Minimum=min,Maximum=max,Mean=m,Tenth_percentile=p10,First_quartile=q1,Second_quartile=q2,
           Third_quartile=q3,Ninetieth_percentile=p90,Ninety_ninth_percentile=p99))}

#-----------------------------------------------------------------------------------------------
dt<-read.csv(fileName,header = T,na.strings="")
dt1<-dt
dt1<-dt1[, colSums( is.na(dt1) ) < nrow(dt1)]
dt1<-dt1[rowSums(is.na(dt1)) != ncol(dt1),]
num_var<-sapply(dt1,is.numeric)
cat_var<-!sapply(dt1,is.numeric)
col_num<-c(names(dt1[num_var]))
col_cat<-c(names(dt1[cat_var]))

st=arg


if(length(grep(",",st))>0){
  v2 <- unlist(c(strsplit(st, split=",")))
}else
{
  v2 <- c(st)
}
dt1 <- dt1[,v2]
stats_num<-as.data.frame(t(data.frame(apply(dt1,2,mystats_numeric))))
#View(stats_num)
# stats_num[ncol(stats_num)+1]<-col_num
# stats_num<-stats_num[,c(ncol(stats_num),1:(ncol(stats_num)-1))]
# colnames(stats_num)[1]<-"Numeric Indicators"
# write.csv(stats_num, file = "c:/out/multi_stat_numeric.csv",row.names=TRUE)
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
nm <- paste("c:/out/All_Numeric_Cols_Stat_",a1,".csv",sep="")
nm1 <- paste("All_Numeric_Cols_Stat_",a1,".csv",sep="")
#===================== file name end============
write.csv(stats_num, file = nm,row.names=TRUE)
return(nm1)

}
