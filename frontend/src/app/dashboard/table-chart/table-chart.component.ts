import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import * as d3 from "d3";
import { Router } from '@angular/router';
declare var $: any;

@Component({
  selector: 'rmncha-table-chart',
  templateUrl: './table-chart.component.html',
  styleUrls: ['./table-chart.component.scss']
})
export class TableChartComponent implements OnInit {
  @ViewChild('tableChart') private chartContainer: ElementRef;
  @Input() private data: any;

  constructor(private hostRef: ElementRef, private router: Router) { }

  ngOnInit() {
    let columns = [];
    if (this.data) {
      this.createChart(this.data, columns);
    }
  }

  sortObject(obj) {
    return Object.keys(obj).sort().reduce(function (result, key) {
      result[key] = obj[key];
      return result;
  }, {});
}
  createChart(data, columns) {
    let thColumns = [];
    let el = this.chartContainer.nativeElement;
    let pageName = this.router.url;
    let tableData: any[] = [];
    let isLevelWise =  data.levelWise;
    let indName = data.chartData[0].headerIndicatorName;
    data = data.chartData[0].chartDataValue;

    if(isLevelWise){

      data.forEach(element => {
        for( let i=0; i< element.length; i++){
          delete element[i].cssClass;
          delete element[i].id;
          delete element[i].legend;
         
            if(element[i].unit == 'Percentage'){
              delete element[i].tooltipValue;
            }else{
              delete element[i].numerator;
              delete element[i].denominator;
            }
          delete element[i].unit;
          element[i].value = element[i].value!=null ? Math.round(element[i].value) : element[i].value;
        }
      });
  
      tableData.push(data[0]);
  
      columns.push(data[0][0].label);

      let levelColumns = [];
      
      if( indName.trim() == 'Percentage of In/outborn admission deaths'){
        levelColumns =  ["", "NBSU (L2)", "SNCU/NBSU (L3)", "Total"];
      }else{
        levelColumns =  ["","L1", "L2", "L3", "Total"];
      }
     
      let levelIndex;
   
      for(let i=0;i<data[0].length;i++){
        tableData[0][i]['0_axis']=tableData[0][i]['axis']
        if(data[0][i].tooltipValue){
          data[0][i].tooltipValue.split(",").forEach(element => {
            if(element.split(":")[0].trim() == "L1"){
              levelIndex = 1;
            }else  if(element.split(":")[0].trim() == "L2"){
              levelIndex = 2;
            }else{
              levelIndex = 3;
            }
            tableData[0][i][levelIndex+"_"+element.split(":")[0].trim()]=element.split(":")[1].trim()
          });
        }
        
        delete tableData[0][i]['tooltipValue'];
        delete tableData[0][i]['axis'];
      }
   
      for(let tb=1; tb<data.length; tb++){
        columns.push(data[tb][0].label);
        for(let i=0;i<data[tb].length;i++){
          tableData[0][i]['value'+i+tb]= data[tb][i].value
        
            // tableData[0][i]['tooltipValue'+i+tb]=data[tb][i].tooltipValue
  
            if(data[tb][i].tooltipValue){
            
              data[tb][i].tooltipValue.split(",").forEach(element => {
                if(element.split(":")[0].trim() == "L1"){
                  levelIndex = 1;
                }else  if(element.split(":")[0].trim() == "L2"){
                  levelIndex = 2;
                }else{
                  levelIndex = 3;
                }
                tableData[0][i][(levelIndex)+"_"+element.split(":")[0].trim()+i+tb]=element.split(":")[1].trim()
              });
          }
       }
      }
      let newTable = [];
      // newTable.push([{}]);
      newTable[0] = [];
      tableData[0].forEach(element => {
  
        newTable[0].push(this.sortObject(element));
      
       });
      //  delete  newTable[0][0];
      //  console.log(newTable);
  
       tableData = [];
       data = [];
       tableData = newTable;
       data = newTable;
       
      thColumns = columns.concat(columns);
      thColumns = thColumns.concat(columns);
      if( indName.trim() != 'Percentage of In/outborn admission deaths'){
        thColumns = thColumns.concat(columns);
      }
      
  
      let lastURLSegment = pageName.substr(pageName.lastIndexOf('/') + 1);
      d3.select(el).selectAll("*").remove();
      // Set the dimensions of the canvas / graph
      let margin = { top: 5, right: 40, bottom: 30, left: 50 },
        width =
          $(this.hostRef.nativeElement).parent().width() - margin.right - margin.left,
        height = ($(this.hostRef.nativeElement).parent().height() );
  
      // Define the svg
      let svg = d3.select(el).append("svg").attr("id", "tableChart")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height)
        .attr("transform",function(){
          if(lastURLSegment == 'snapshot-view'){
            return  "translate(0," + (margin.top - 5) + ")";
          }else{
            return  "translate(" + margin.top + ",0)";
          }
        })
        .append("g")
        .attr("transform","translate(" + margin.top + ",0)")
         
  
     // append table
      let table = svg
      .append("foreignObject")
        .attr("width", width+ margin.left + margin.right)
        .attr("height", height)
        .style("overflow","auto")
        .append("xhtml:div").append("table")
        .attr("width", width+ margin.left + margin.right)
        .attr("height", height),
  
      // append head and body
        thead = table.append("thead")
        .style("text-align", "center")
        .style("background","#495769")
        .style("color","#fff"),
        tbody = table.append("tbody");
  
      // append the level header row
  
      //this is for a specific requirement from client
      let ind = indName.trim() == 'Clinical posts (In position Vs. Sanctioned)' ? ['Cadre'] :
         indName.trim() == 'Para medical Posts (In Position Vs. Sanctioned)' ?
       ['Cadre'] : ['Indicator'];
     let tr = thead.append("tr")
     tr .style("height","20px")
      .selectAll("th")
      .data(ind)
      .enter()
      .append("th")
      .attr("rowspan", 2)
      .style("font-size","13px")
      .style("border","1px solid #ddd")
      .attr("font-family", "'Questrial', sans-serif")
      .text(function (indcel) { return indcel; });
  
      tr
      .style("height","20px")
      .selectAll("th")
      .data(levelColumns)
      .enter()
      .append("th")
      .attr("colspan", columns.length)
      .style("font-size","13px")
      .style("border","1px solid #ddd")
      .attr("font-family", "'Questrial', sans-serif")
      .text(function (levelColumn) { return levelColumn; });
  
       // append the header row
      thead.append("tr")
      .style("height","20px")
      .selectAll("th")
      .data(thColumns)
      .enter()
      .append("th")
      .style("font-size","13px")
      .style("border","1px solid #ddd")
      .attr("font-family", "'Questrial', sans-serif")
      .text(function (column) { return column; });
  
      for(let c=0; c<data.length; c++){
        for(let nest=0; nest<data[c].length; nest++){
          delete data[c][nest].label;
        }
           // create a row for each object in the data
      let rows = tbody.selectAll("tr")
      .data(data[c])
      .enter()
      .append("tr")
      .style("background","#5feac3")
      .attr("font-family", "'Questrial', sans-serif")
      .style("border-bottom","1px solid #ffffff57")
      .style("height",(height/(2*tableData[0].length))+10+'px');
  
    // create a cell in each row for each column
       rows.selectAll("td")
      .data(d => d3.values(d))
      .enter()
      .append("td")
      .style("font-size","12px")
      .style("border","1px solid #ddd")
      .attr("font-family", "'Questrial', sans-serif")
      .style("height",(height/(2*tableData[0].length))+5+'px')
      // .style("width","200px")
      .style("width",function(d){
        if(d!=null){
          let letterNumber = /^[0-9]\d*(\.\d+)?$/;
        if(!letterNumber.test(d)){
          return ((width+ margin.left + margin.right/2)+50)+'px'
        }else{
            return ((width+ margin.left + margin.right/2)-50)+'px';
          }
        }else{
          return ((width+ margin.left + margin.right/2)-50)+'px';
        }
      })
      .style("text-align",function(d){
        if(d!=null){
          let letterNumber = /^[0-9]\d*(\.\d+)?$/;
        if(letterNumber.test(d) || d=='NA'){
          return "center"
        }else{
          return "left"
        }
        }else{
          return "center"
        }
      })
      .style("padding-left",function(d){
        if(d!=null){
          let letterNumber = /^[0-9]\d*(\.\d+)?$/;
        if(!letterNumber.test(d)){
          return "5px"}
        }
      })
      .text(d => d!=null?d:'NA')
  
  ;}
  return table;
    }else{

      columns = ["Indicator"];
      data.forEach(element => {
        for( let i=0; i< element.length; i++){
          delete element[i].cssClass;
          delete element[i].id;
          delete element[i].legend;
          delete element[i].tooltipValue;
          delete element[i].numerator;
          delete element[i].denominator;
          delete element[i].unit;
          element[i].value = element[i].value!=null ? Math.round(element[i].value) : element[i].value;
        }
      });
  
      tableData.push(data[0]);
  
      columns.push(data[0][0].label);
     
      for(let tb=1; tb<data.length; tb++){
        columns.push(data[tb][0].label);
       for(let i=0;i<data[tb].length;i++){
        tableData[0][i]['value'+i+tb]= data[tb][i].value
       }
      }
      let lastURLSegment = pageName.substr(pageName.lastIndexOf('/') + 1);
      d3.select(el).selectAll("*").remove();
      // Set the dimensions of the canvas / graph
      let margin = { top: 5, right: 40, bottom: 30, left: 50 },
        width =
          $(this.hostRef.nativeElement).parent().width() - margin.right - margin.left,
        height = ($(this.hostRef.nativeElement).parent().height() );
  
      // Define the svg
      let svg = d3.select(el).append("svg").attr("id", "tableChart")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height)
        .attr("transform",function(){
          if(lastURLSegment == 'snapshot-view'){
            return  "translate(0," + (margin.top - 5) + ")";
          }else{
            return  "translate(" + margin.top + ",0)";
          }
        })
        .append("g")
        .attr("transform","translate(" + margin.top + ",0)")
         
  
     // append table
      let table = svg
      .append("foreignObject")
        .attr("width", width+ margin.left + margin.right)
        .attr("height", height)
        .style("overflow","auto")
        .append("xhtml:div").append("table")
        .attr("width", width+ margin.left + margin.right)
        .attr("height", height),
  
      // append head and body
        thead = table.append("thead")
        .style("text-align", "center")
        .style("background","#495769")
        .style("color","#fff"),
        tbody = table.append("tbody");
  
      // append the header row
  
  
      thead.append("tr")
      .style("height","20px")
      .selectAll("th")
      .data(columns)
      .enter()
      .append("th")
      .style("font-size","13px")
      .style("border","1px solid #ddd")
      .attr("font-family", "'Questrial', sans-serif")
      .text(function (column) { return column; });
  
      for(let c=0; c<data.length; c++){
        for(let nest=0; nest<data[c].length; nest++){
          delete data[c][nest].label;
        }
           // create a row for each object in the data
      let rows = tbody.selectAll("tr")
      .data(data[c])
      .enter()
      .append("tr")
      .style("background","#5feac3")
      .attr("font-family", "'Questrial', sans-serif")
      .style("border-bottom","1px solid #ffffff57")
      .style("height",(height/(2*tableData[0].length))+10+'px');
  
    // create a cell in each row for each column
       rows.selectAll("td")
      .data(d => d3.values(d))
      .enter()
      .append("td")
      .style("font-size","12px")
      .style("border","1px solid #ddd")
      .attr("font-family", "'Questrial', sans-serif")
      .style("height",(height/(2*tableData[0].length))+5+'px')
      // .style("width","200px")
      .style("width",function(d){
        if(d!=null){
          let letterNumber = /^[0-9]\d*(\.\d+)?$/;
        if(!letterNumber.test(d)){
          return ((width+ margin.left + margin.right/2)+50)+'px'
        }else{
            return ((width+ margin.left + margin.right/2)-50)+'px';
          }
        }else{
          return ((width+ margin.left + margin.right/2)-50)+'px';
        }
      })
      .style("text-align",function(d){
        if(d!=null){
          let letterNumber = /^[0-9]\d*(\.\d+)?$/;
        if(letterNumber.test(d)){
          return "center"
        }else{
          return "left"
        }
        }else{
          return "center"
        }
      })
      .style("padding-left",function(d){
        if(d!=null){
          let letterNumber = /^[0-9]\d*(\.\d+)?$/;
        if(!letterNumber.test(d)){
          return "5px"}
        }
      })
      .text(d => d!=null?d:'NA')
  ;}
      return table;
    }

  
  
  
  }

}
