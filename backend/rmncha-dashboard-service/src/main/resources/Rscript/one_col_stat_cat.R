begin1 <- function(arg,fileName)
{



one_cat_op<-function(v)
{
  v<-as.numeric(v)
  v<-dt1[,c(v)]
  if (class(v)=="character"||class(v)=="factor")
  {
    newdt<-data.frame(v)
    uniqv <- unique(as.character(v))
    m1<-uniqv[which.max(tabulate(match(v, uniqv)))]
    pt<-as.data.frame(prop.table(table(newdt[1])))
    pro<-(pt[which(pt$Var1==m1),"Freq"])
    nmiss1<-sum(is.na(v))
    ss<-nrow(dt1)-nmiss1
    return(c(Max_Freq=m1,Proportion=pro,missing_values=nmiss1,Sample_size=ss))
    
  }
  else{
    return(0)
  }
}



dt<-read.csv(fileName,header = T,na.strings="")
dt1<-dt
dt1<-dt1[, colSums( is.na(dt1) ) < nrow(dt1)]
dt1<-dt1[rowSums(is.na(dt1)) != ncol(dt1),]
num_var<-sapply(dt1,is.numeric)
cat_var<-!sapply(dt1,is.numeric)
col_num<-c(names(dt1[num_var]))
col_cat<-c(names(dt1[cat_var]))



# arg="2"
y1=one_cat_op(arg)

if(class(y1)=="numeric")
{
  return("Please enter a Categorical variable")
}else
{
  y1<-as.data.frame(y1)
  names(y1)<-names(dt1[as.numeric(arg)])
  # View(y1)
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
  nm <- paste("c:/out/one_col_stat_catagory_",a1,".csv",sep="")
  nm1 <- paste("one_col_stat_catagory_",a1,".csv",sep="")
  #===================== file name end============
  write.csv(y1, file = nm,row.names=TRUE)
  return(nm1)
}

}
