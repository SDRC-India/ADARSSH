begin1 <- function(arg,fileName)
{
require(VIM)

dt<-read.csv(fileName,header = T,na.strings="")

dt1<-dt



dt1<-dt1[, colSums( is.na(dt1) ) < nrow(dt1)]
dt1<-dt1[rowSums(is.na(dt1)) != ncol(dt1),]
num_var<-sapply(dt1,is.numeric)
cat_var<-!sapply(dt1,is.numeric)
col_num<-c(names(dt1[num_var]))
col_cat<-c(names(dt1[cat_var]))



dt3<-dt1[,num_var]
max_min_vec<-vector()
min_vec<-vector()
for(i in 1:ncol(dt3))
{
  a<-dt3[i][!is.na(dt3[i])]
  m1<-min(a)
  m2<-max(a)
  max_min_vec<-c(max_min_vec,m2-m1)
  min_vec<-c(min_vec,m1)
  for(j in 1:nrow(dt3))
  {
    if(!is.na(dt3[j,i]))
      dt3[j,i]<-(dt3[j,i]-m1)/(m2-m1)
  }
}
dt3<-cbind(dt3,dt1[,cat_var])
# View(dt3)
dt2<-kNN(dt3,k=11)
dt1<-subset(dt2,select= c(names(dt1[1:ncol(dt1)])))
dt3<-dt1[,num_var]
# View(dt1)
for(i in 1:ncol(dt3))
{
  for(j in 1:nrow(dt3))
  {
    dt3[j,i]<-(dt3[j,i]*max_min_vec[i])+min_vec[i]
  }
}
dt1<-cbind(dt3,dt1[,cat_var])
# View(dt1)
Time=Sys.time()

dd = format(as.POSIXct(Time,format="%d/%m/%Y"),"%d")
mm = format(as.POSIXct(Time,format="%d/%m/%Y"),"%m")
yy = format(as.POSIXct(Time,format="%d/%m/%Y"),"%Y")
hour = format(as.POSIXct(Time,format="%H:%M:%S"),"%H")
minute = format(as.POSIXct(Time,format="%H:%M:%S"),"%M")
sec = format(as.POSIXct(Time,format="%H:%M:%S"),"%S")
a1=paste(dd,mm,yy,hour,minute,sec,sep="_")

st=arg


if(length(grep(",",st))>0){
  v2 <- unlist(c(strsplit(st, split=",")))
}else
{
  v2 <- c(st)
}

dt1 <- dt1[,v2,drop=FALSE]

nm <- paste("c:/out/treated_data_",a1,".csv",sep="")
nm1 <- paste("treated_data_",a1,".csv",sep="")

write.csv(dt1,file = nm,row.names=TRUE)

return(nm1)
}