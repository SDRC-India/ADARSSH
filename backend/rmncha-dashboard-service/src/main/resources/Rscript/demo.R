# f1 <- function()
# {
#   write.csv("one", "d:/out/x1.csv",row.names=FALSE)
#   return ("sriram")
# }
# 
# f2 <- function()
# {
#   write.csv("one", "d:/out/x2.csv",row.names=FALSE)
#   return ("sitaram")
# }
# 
# begin1 <- function()
# {
#   s <- f1()
#   write.csv(s, "d:/out/x3.csv",row.names=FALSE)
#   
#   s <- f2()
#   write.csv(s, "d:/out/x4.csv",row.names=FALSE)
#   # print(s)
#   # write.csv(dt1, file = "d:/out/x2.csv",row.names=FALSE)
#   # f1()
#   return ("jai sriram")
# }

# mystats_numeric<-function(x)
# {
#   nmiss<-sum(is.na(x))
#   a<-x[!is.na(x)]
#   m<-mean(a)
#   min<-min(a)
#   max<-max(a)
#   s<-sd(a)
#   v<-var(a)
#   p1<-quantile(a,0.01)
#   p2<-quantile(a,0.05)
#   p10<-quantile(a,0.10)
#   q1<-quantile(a,0.25)
#   q2<-quantile(a,0.50)
#   q3<-quantile(a,0.75)
#   p90<-quantile(a,0.90)
#   p95<-quantile(a,0.95)
#   p99<-quantile(a,0.99)
#   UC<-round(m+3*s,0)
#   LC<-round(m-3*s,0)
#   outliers<-sum(!is.na(x[x>UC | x<LC]))
#   return(c(Number_of_missing_values=nmiss,Standard_deviation=s,Variance=v,Number_of_outliers=outliers,
#            Minimum=min,Maximum=max,Mean=m,Tenth_percentile=p10,First_quartile=q1,Second_quartile=q2,
#            Third_quartile=q3,Ninetieth_percentile=p90,Ninety_ninth_percentile=p99))}
# 
# 
# begin1 <- function()
# {
#   dt<-read.csv(file.choose(),header = T,na.strings="")
#   dt1<-dt
#   num_var<-sapply(dt1,is.numeric)
#   cat_var<-!sapply(dt1,is.numeric)
#   col_num<-c(names(dt1[num_var]))
#   col_cat<-c(names(dt1[cat_var])) 
#   stats_num<-t(data.frame(apply(dt1[col_num],2,mystats_numeric)))
#   write.csv(stats_num, file = "d:/out/stat_numeric.csv",row.names=FALSE)
# }


# mystats_numeric_one_col<-function(x)
# {
#   
#   x<-as.numeric(x)
#   x<-dt1[,c(x)]
#   if (class(x)=="character"||class(x)=="factor")
#   {
#     return(0)
#   }
#   else{
#     nmiss<-sum(is.na(x))
#     a<-x[!is.na(x)]
#     m<-mean(a)
#     min<-min(a)
#     max<-max(a)
#     s<-sd(a)
#     v<-var(a)
#     p1<-quantile(a,0.01)
#     p2<-quantile(a,0.05)
#     p10<-quantile(a,0.10)
#     q1<-quantile(a,0.25)
#     q2<-quantile(a,0.50)
#     q3<-quantile(a,0.75)
#     p90<-quantile(a,0.90)
#     p95<-quantile(a,0.95)
#     p99<-quantile(a,0.99)
#     UC<-round(m+3*s,0)
#     LC<-round(m-3*s,0)
#     outliers<-sum(!is.na(x[x>UC | x<LC]))
#     write.csv("chandi", "d:/out/new1.csv",row.names=FALSE)
#     return ("rama")
#     # return(y=data.frame(c(Number_of_missing_values=nmiss,Standard_deviation=s,Variance=v,Number_of_outliers=outliers,
#     #                       Minimum=min,Maximum=max,Mean=m,Tenth_percentile=p10,First_quartile=q1,Second_quartile=q2,
#     #                       Third_quartile=q3,Ninetieth_percentile=p90,Ninety_ninth_percentile=p99)))
#   }
# }

set_col <- function(arg)
{
  dt<-read.csv(arg,header = T,na.strings="")
  dt<-dt
  dt<-dt[, colSums( is.na(dt) ) < nrow(dt)]
  dt<-dt[rowSums(is.na(dt)) != ncol(dt),]
  cols<-list("char"= c(names(dt[!sapply(dt,is.numeric)])),"num"= c(names(dt[sapply(dt,is.numeric)])),"total"=names(dt))
  
  return(cols)
  
}

begin1 <- function(arg,fileName)
{
  # dt<-read.csv(file.choose(),header = T,na.strings="")
  dt<-read.csv(fileName,header = T,na.strings="")
  dt1<-dt
  num_var<-sapply(dt1,is.numeric)
  cat_var<-!sapply(dt1,is.numeric)
  col_num<-c(names(dt1[num_var]))
  col_cat<-c(names(dt1[cat_var]))

  mystats_numeric_one_col<-function(x)
  {
    
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
      # write.csv("chandi", "d:/out/new1.csv",row.names=FALSE)
      # return ("rama")
      return(y=data.frame(c(Number_of_missing_values=nmiss,Standard_deviation=s,Variance=v,Number_of_outliers=outliers,
                            Minimum=min,Maximum=max,Mean=m,Tenth_percentile=p10,First_quartile=q1,Second_quartile=q2,
                            Third_quartile=q3,Ninetieth_percentile=p90,Ninety_ninth_percentile=p99)))
    }
  }
  
  
  
   # arg="3"
  y1 <- mystats_numeric_one_col(arg)

 
  if(y1==0)
  {
    # print("Please enter a Numeric variable")
    return("Please enter a Numeric variable")
  
  }else
  {
    y1<-as.data.frame(y1)
    names(y1)<-names(dt1[as.numeric(arg)])
    
    # write.csv(y1, "d:/out/one_col_stat_numeric.csv",row.names=TRUE)
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
    nm1 <- paste("one_col_stat_numeric_",a1,".csv",sep="")
    #===================== file name end============
    write.csv(y1, file = nm,row.names=TRUE)
    return(nm1)
  }
}
