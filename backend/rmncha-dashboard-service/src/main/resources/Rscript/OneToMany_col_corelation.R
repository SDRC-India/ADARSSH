begin1 <- function(arg,arg1,fileName)
{

  #options(warn=-1)  
out <- tryCatch(
{
stat_out<-function(param){
  R_square<-vector()
  Adj_square<-vector()
  Sigma<-vector()
  F_stat<-vector()
  P_value<-vector()
  Aic<-vector()
  Bic<-vector()
  Deviance<-vector()
  DF_Residual<-vector()
  for(i in param$MyFits){
    R_square<-c(R_square,i$r.squared)
    Adj_square<-c(Adj_square,i$adj.r.squared)
    P_value<-c(P_value,i$p.value)
    F_stat<-c(F_stat,i$statistic)
    Sigma<-c(Sigma,i$sigma)
    Aic<-c(Aic,i$AIC)
    Bic<-c(Bic,i$BIC)
    Deviance<-c(Deviance,i$deviance)
    DF_Residual<-c(DF_Residual,i$df.residual)
  
  }
  # df<-data.frame(R_square,Adj_square,P_value,F_stat,Sigma,Aic,Bic,Deviance,DF_Residual)
  df<-data.frame(R_square,Adj_square,P_value,F_stat,Sigma)
  return(df)
}

Col_reln<-function(z)
{
  
  
  
  
  z<-as.numeric(z)
  y<-dt1[,c(z)]
  if(is.numeric(y))
  {
    
    #Fits <- as.data.table(dt1)[, list(MyFits = lapply(.SD[, with = F], function(x) glance(lm(y~ x))))]
    Fits <- as.data.table(dt1)[, list(MyFits = lapply(.SD[, with = F], function(x) glance(lm(y~ x))))]
    write.csv(arg, "c:/out/ffff1.csv",row.names=TRUE)
    regrn_out<-stat_out(Fits)
    variable<-names(dt1)
    regrn_out<-cbind(variable,regrn_out)
    # View(regrn_out)
   
    
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
    nm <- paste("c:/out/linear_relation_between_one_and_allother_vars_",a1,".csv",sep="")
    nm1 <- paste("linear_relation_between_one_and_allother_vars_",a1,".csv",sep="")
    #===================== file name end============
    write.csv(regrn_out, file = nm,row.names=TRUE)
    return(nm1)
    # return(regrn_out)
    
    
  }
  else
  {
    return("Please select a numeric variable")
  }
  
  #write.csv()
}

require(VIM)
require(broom)
require(data.table)
dt<-read.csv(fileName,header = T,na.strings="")
# dt<-read.csv("e:/R_Input/SSV_Data_IPE_1.csv",header = T,na.strings="")
dt1<-dt
dt1<-dt1[, colSums( is.na(dt1) ) < nrow(dt1)]
dt1<-dt1[rowSums(is.na(dt1)) != ncol(dt1),]
num_var<-sapply(dt1,is.numeric)
cat_var<-!sapply(dt1,is.numeric)
col_num<-c(names(dt1[num_var]))
col_cat<-c(names(dt1[cat_var]))



# MISSING VALUE TREATMENT ----------------------------------------------------------------------

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

# class(dt1$Total.Deliveries)
# View(dt1)

#write.csv(dt1,"treated_data.csv")

#-----------------------------------------------------------------------------------
# arg1="SBA,PPIUCD,RKSK"
# arg="SBA"
# Col_reln("2")
# dt1[,cat_var]<-lapply(dt1[,cat_var],as.factor)
# Col_reln(arg)

st=arg1
if(length(grep(",",st))>0){
  v2 <- unlist(c(strsplit(st, split=",")))
  dt1 <- dt1[,v2]
}else{
  
}

# st="Live.births,Normal,C.section"
# st="SBA,PPIUCD,RKSK"
# Col_reln("2")



v1= names(dt1)
z1=Col_reln(match(arg, v1))
# write.csv(z1, "c:/out/colrel.csv",row.names=TRUE)
return(z1)
},
error=function(cond) {
  return(cond)
})

return(out)

}
#-----------------------------------------------------------------------------------

