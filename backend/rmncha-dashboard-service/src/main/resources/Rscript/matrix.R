begin1 <- function(arg,fileName)
{
  dt<-read.csv(fileName,header = T,na.strings="")
  # dt<-read.csv(file.choose(),header = T,na.strings="")
  dt1<-dt
  dt1<-dt1[, colSums( is.na(dt1) ) < nrow(dt1)]
  dt1<-dt1[rowSums(is.na(dt1)) != ncol(dt1),]
  num_var<-sapply(dt1,is.numeric)
  cat_var<-!sapply(dt1,is.numeric)
  col_num<-c(names(dt1[num_var]))
  col_cat<-c(names(dt1[cat_var]))
  mat<-matrix(,nrow=0,ncol=3)
  # mat<-rbind(mat,c(1,2,3))
  
  # View(dt1)
  # st="BCG,OPV,IPV"
  # st="BCG"
  st=arg
  
  # nm2[1]
  # st="SBA,PPIUCD,RKSK"
  # st="SBA"
  if(length(grep(",",st))>0){
    v2 <- unlist(c(strsplit(st, split=",")))
    dt2 <- dt1[,v2]
    
    
    # dt2 <-dt1[,num_var] 
    # View(dt2)
    for(i in 1:ncol(dt2))
    {
      a<-dt2[i][!is.na(dt2[i])]
      m<-mean(a)
      s<-sd(a)
      UC<-round(m+3*s,0)
      LC<-round(m-3*s,0)
      for(j in 1:nrow(dt2))
      {
        if((dt2[j,i] > UC || dt2[j,i] < LC) && !is.na(dt2[j,i]))
        {
          # print(j)
          if(dt2[j,i] > UC)
          {
            mat<-rbind(mat,c(j,names(dt2[i]),dt2[j,i],UC))
          }
          else
          {
            mat<-rbind(mat,c(j,names(dt2[i]),dt2[j,i],LC))
          }
        }
      }
    }
    
    
    
  }else
  {
    v2 <- c(st)
    nm2=names(dt1)
    v2 <- c(v2,nm2[1])
    dt2 <- dt1[,v2]
    a<-dt2[1][!is.na(dt2[1])]
    m<-mean(a)
    s<-sd(a)
    UC<-round(m+3*s,0)
    LC<-round(m-3*s,0)
    for(j in 1:nrow(dt2))
    {
      if((dt2[j,1] > UC || dt2[j,1] < LC) && !is.na(dt2[j,1]))
      {
        # print(j)
        if(dt2[j,1] > UC)
        {
          mat<-rbind(mat,c(j,names(dt2[1]),dt2[j,1],UC))
        }
        else
        {
          mat<-rbind(mat,c(j,names(dt2[1]),dt2[j,1],LC))
        }
      }
    }
    # View(mat)
    
  }
  
  
  mat<-as.data.frame(mat)
  names(mat)<-c("Row_Number","Column_Name","Value")
  
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
  nm <- paste("c:/out/Outlier_Matrix_",a1,".csv",sep="")
  nm1 <- paste("Outlier_Matrix_",a1,".csv",sep="")
  #===================== file name end============
  
  
  write.csv(mat, file = nm ,row.names=FALSE)
  return(nm1)
}
