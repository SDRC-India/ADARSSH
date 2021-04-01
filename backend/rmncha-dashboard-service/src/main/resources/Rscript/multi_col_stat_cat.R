
begin1 <- function(arg,fileName)
{

cat_op<-function(v)
{
  newdt<-data.frame(v)
  uniqv <- unique(as.character(v))
  m1<-uniqv[which.max(tabulate(match(v, uniqv)))]
  pt<-as.data.frame(prop.table(table(newdt[1])))
  pro<-(pt[which(pt$Var1==m1),"Freq"])
  nmiss1<-sum(is.na(v))
  return(c(Max_Freq=m1,Proportion=pro,missing_values=nmiss1))
}



dt<-read.csv(fileName,header = T,na.strings="")
# dt<-read.csv("e:/R_Input/SSV_Data_IPE_1.csv",header = T,na.strings="")
dt1<-dt
dt1<-dt1[, colSums( is.na(dt1) ) < nrow(dt1)]
dt1<-dt1[rowSums(is.na(dt1)) != ncol(dt1),]
num_var<-sapply(dt1,is.numeric)
cat_var<-!sapply(dt1,is.numeric)
col_num<-c(names(dt1[num_var]))
col_cat<-c(names(dt1[cat_var]))


# View(dt1)
# st="BCG,OPV,IPV"
# st="BCG"
st=arg


if(length(grep(",",st))>0){
  v2 <- unlist(c(strsplit(st, split=",")))
}else
{
  v2 <- c(st)
}
dt1 <- dt1[,v2]
stats_cat<-t(data.frame(apply(dt1,2,cat_op)))
stats_cat<-as.data.frame(stats_cat)
stats_cat$sample_size<-nrow(dt1)-as.numeric(as.character(stats_cat$missing_values))
# View(stats_cat)

# stats_cat<-t(data.frame(apply(dt1[col_cat],2,cat_op)))
# stats_cat<-as.data.frame(stats_cat)
# stats_cat$sample_size<-nrow(dt1)-as.numeric(as.character(stats_cat$missing_values))



#View(stats_cat)
# stats_cat[ncol(stats_cat)+1]<-col_cat
# stats_cat<-stats_cat[,c(ncol(stats_cat),1:(ncol(stats_cat)-1))]
# colnames(stats_cat)[1]<-"Categorical Indicators"
# write.csv(stats_cat, file = "c:/out/stat_categorical.csv",row.names=TRUE)

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
nm <- paste("c:/out/All_Catagorical_Cols_Stat_",a1,".csv",sep="")
nm1 <- paste("All_Catagorical_Cols_Stat_",a1,".csv",sep="")
#===================== file name end============
write.csv(stats_cat, file = nm,row.names=TRUE)
return(nm1)



}