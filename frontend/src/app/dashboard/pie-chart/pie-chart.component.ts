import { Component, OnInit, Input, ViewEncapsulation, ElementRef,EventEmitter, ViewChild, Output } from "@angular/core";
import { HttpClient } from '@angular/common/http';
import * as d3 from "d3";
// import * as d3plus from "d3plus-text";
declare var $: any;

@Component({
  selector: 'rmncha-pie-chart',
  templateUrl: './pie-chart.component.html',
  styleUrls: ['./pie-chart.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class PieChartComponent implements OnInit {

  @ViewChild('pieChart') private chartContainer: ElementRef;
  @Output() allZeroValueConvertToBar:  EventEmitter<any> = new EventEmitter<any>();
  @Input() private data: any;

  constructor(private httpClient: HttpClient, private hostRef: ElementRef) { }

  ngOnInit() {
    if (this.data) {
      this.createChart(this.data);
    }
  }

  createChart(data) {
    let legends = data.chartData[0].legends;
    let headerIndicatorName= data.chartData[0].headerIndicatorName
    let chartType = data.chartsAvailable[0];
    let n = legends, length;
    data = data.chartData[0].chartDataValue[0];
    // legend dimensions
    let legendRectSize = 25; // defines the size of the colored squares in legend
    let legendSpacing = 6; // defines spacing between squares
    let el = this.chartContainer.nativeElement;
    let viewportWidth = $(window).width();
    let divWidth = $(this.hostRef.nativeElement).parent().width();
    d3.select(el).selectAll("*").remove();


    let margin = {
      left: 20,
      right: 20,
      top: -20,
      bottom: 20
    }
   let width =
    $(this.hostRef.nativeElement).parent().width() - margin.right - margin.left,
    height = ($(this.hostRef.nativeElement).parent().height() - 50);
    margin.left = (($(this.hostRef.nativeElement).parent().width() - width) / 2) - 90;
    margin.right = ($(this.hostRef.nativeElement).parent().width() - width) / 2;
    let radius = Math.min(width, height) / 2;
    let color = d3.scaleOrdinal().range(
      ["#c26b61", "#F37361", "#F4A775", "#F4C667"]);
      let chartColor = {
      "Enrolled": "#aedc5c",
      "Left Out": "#f07258"
    }
    // set width, height
    let y = d3.scaleBand().domain(d3.range(10, n)).range([height, -60])
    
    let arc;
    if(chartType == "donut"){
      arc = d3.arc()
      .outerRadius(radius).innerRadius(90).cornerRadius(3)
      .padAngle(0.015);
    }else if(chartType == "pie"){
      arc = d3.arc()
      .outerRadius(radius).innerRadius(0).cornerRadius(3)
      .padAngle(0.015);
    }
    
    let pie = d3.pie()
      .value(function (d) {
          return parseFloat(d.value);
      }).sort(null);


      let svg = d3
      .select(el)
      .append("svg")
      .attr("id", "chart")
      .attr("width", width+20)
      .attr("height", function () {
        if (viewportWidth > 1024) {
          return height + 80
        } else if (viewportWidth == 1024) {
          return height + 10
        } else if (viewportWidth < 1000) {
          return height + 40
        }
      })
      .attr('transform', function(){
        if(data[0].value != null && headerIndicatorName == "Referral of Malnourished children" && viewportWidth > 700){
          return "translate(4.0008," + (margin.top - 265) + ")"
        }else{
          return "translate(4.0008," + (margin.top + 5) + ")"
        }
      })
       
      // .attr('transform', 'translate(' + (margin.left+90) + ',' + margin.top + ')')

      .append("g")
      .attr("transform", function () {
        if (viewportWidth > 450) {
          return "translate(" + (width / 2) + "," + (height / 2 + 20) + ")"
        } else {
          return "translate(" + (width / 2+ 20)+ "," + (height / 2 + 25) + ")"
        }
      });


    //check for no data availble
    let allNullValues = true;
    let allZeroValues =  true;
    for (let j = 0; j < data.length; j++) {
      if (data[j].value != null) {
        allNullValues = false;
        break;
      }
    }
    for (let j = 0; j < data.length; j++) {
      if (data[j].value != 0) {
        allZeroValues = false;
        break;
      }
    }
    if (allZeroValues) {
      let selectedChart='bar' ;
      this.allZeroValueConvertToBar.emit(selectedChart);
    }

    if (allNullValues) {
      svg.append("text")
        .attr("transform", "translate(20,-35)")
        .attr("x", 0)
        .attr("y", 30)
        .attr("font-size", "28px")
        .style("text-anchor", "middle")
        .text("Data Not Available");
      return;
    }
    function pieChart(data) {
      let g = svg.selectAll(
        ".arc").data(
          pie(data))
        .enter()
        .append("g")
        .attr("class", "arc")
        .attr("align", "left");

      g.append("path")
        .attr("d", arc)
        .attr("class", "pie-path")
        .style("cursor", (d) => {
          if (d.value) {
            return 'pointer'
          }
        })
        .on("mouseover", function (d, i) {
          showPopover.call(this, d, i)
          d3.select(this).style("opacity","1");
        })
        .on("mouseout", function (d) {
          removePopovers();
          d3.select(this).style("opacity","0.9");
        })
        //   .on("click",
        //     click)
        .transition()
        .delay(
          function (d, i) {
            return i * 1;
          })
        .duration(1000)
        .attrTween('d', function (d) {
          let i = d3.interpolate(d.startAngle + 0.1, d.endAngle);
          return function (t) {
            d.endAngle = i(t);
            return arc(d);
          };
        })
        .attr("class", function (d) {
          if (d.endAngle)
            return d.data.label;
          else
            return d.data.label
              + " zeroValue";
        })
        .style("opacity","0.9")
        .style("fill", function (d,i) {
         if(d.value == 0){
          return "#5538251a";
         }else{
          return legends[i].cssClass;
         }
          
        })
      // .style("fill", function (d) {
      //   return d.data ? d.data.colorCode:'#333';
      // })
      svg.selectAll(".arc").append("text")
        .attr("transform", function (d) {
          return "translate(" + arc.centroid(d) + ")";
        })
        .attr("dx", "-1em")
        .attr("dy", "0.5em")
        .text(function (d) {
          if (d.data.value != 0 && d.data.value != null){
            return d.data.unit == 'Percentage' ? Math.round(d.data.value) + "%" :Math.round(d.data.value) ;
          }
        });

      function showPopover(d, i) {
        $(this).popover(
          {
            title: '',
            placement: 'top',
            container: 'body',
            trigger: 'manual',
            html: true,
            animation: true,
            content: function () {
              if (d.data.axis != '' && d.data.denominator != null && d.data.numerator != null && d.data.unit == 'Percentage') {
                return "<div style='color: #495769;'>" + "<b>" + d.data.axis + "</b>" + "</div>" +
                  "<div>" + " Data Value : " + "<span style='color: #495769;font-weight:500;'>" + d.data.tooltipValue +" (%)"+ "</span>" + "</div>" +
                  "<div>" + "Numerator : " + "<span style='color: #495769;font-weight:500'>" + d.data.numerator + "</span>" + "</div>" +
                  "<div>" + "Denominator : " + "<span style='color: #495769;font-weight:500'>" + d.data.denominator + "</span>" + "</div>";
              } else if (d.data.denominator == null && d.data.numerator == null && d.data.unit == 'Percentage') {
                return "<div style='color: #495769;'>" + "<b>" + d.data.axis + "</b>" + "</div>" +
                  "<div>" + " Data Value : " + "<span style='color: #495769;font-weight:500;'>" + d.data.tooltipValue +" (%)"+ "</span>" + "</div>";
              } else if (d.data.denominator == null && d.data.numerator != null && d.data.unit == 'Percentage') {
                return "<div style='color: #495769;'>" + "<b>" + d.data.axis + "</b>" + "</div>" +
                  "<div>" + " Data Value : " + "<span style='color: #495769;font-weight:500;'>" + d.data.tooltipValue +" (%)"+ "</span>" + "</div>" +
                  "<div>" + "Numerator : " + "<span style='color: #495769;font-weight:500'>" + d.data.numerator + "</span>" + "</div>";
              } else if (d.data.denominator != null && d.data.numerator == null && d.data.unit == 'Percentage') {
                return "<div style='color: #495769;'>" + "<b>" + d.axis + "</b>" + "</div>" +
                  "<div>" + " Data Value : " + "<span style='color: #495769;font-weight:500;'>" + d.data.tooltipValue +" (%)"+ "</span>" + "</div>" +
                  "<div>" + "Denominator : " + "<span style='color: #495769;font-weight:500'>" + d.data.denominator + "</span>" + "</div>";
              }
              else {
                return "<div style='color: #495769;'>" + "<b>" + d.data.axis + "</b>" + "</div>" +
                "<div style='color: #495769;'> Data Value: " + d.data.tooltipValue + "</div>";
              }
            }
          });
        $(this).popover('show');
      }
      function removePopovers() {
        $('.popover').each(function () {
          $(this).remove();
        });
      }
      $(".percentVal")
        .delay(1500)
        .fadeIn();
    }
    // add legend   
	// var legend = svg.append("g")
  // .attr("class", "legend")
  // .attr("x", width - 350)
  // .attr("y", 125)
  // .attr("height", 100)
  // .attr("width", 200);

// legend.selectAll('g').data(legends)
//     .enter()
//     .append('g')
//     .each(function(d, i) {
//       var g = d3.select(this);
//       g.append("rect")
//         .attr("x", width - 350)
//         .attr("y", i*35)
//         .attr("width", 10)
//         .attr("height", 10)
//         .style("fill", function(d){
//           return d.cssClass
//         });
      
    //   g.append("text")
    //     .attr("x", width - 335)
    //     .attr("y", i * 35 + 2)
    //     .attr("height",100)
    //     .attr("width",200)
    //     .style("font-size",13)
    //     .style("fill",'#000')
    //     .style("cursor","pointer")
    //     // .text(d.value)
    //     // .call(wrap, width + margin.left, width);
    //     .each(function (d, i) {
    //       var lines = wordwrap(d.value)
    //       for (var j = 0; j < lines.length; j++) {
    //         d3.select(this).append("tspan")
    //         .style("line-height",1)
    //         .style("width",width/2 - 100)
    //             .attr("dy","8")
    //             .attr("x",function(d,i) { 
    //                return 163; })
    //               .text(lines[j])
    //       }
    //     })

    // });

    function wordwrap(text) {
      let lines=text.split(/\s+/)
      return lines
    }
   

    pieChart(data);
    function wrap(text, width, windowWidth) {
      text.each(function () {
        let text = d3.select(this),
          words = text.text().split(/\s+/).reverse(),
          word,
          cnt = 0,
          line = [],
          textLength = text.node().getComputedTextLength(),
          lineNumber = 0,
          lineHeight = 1,
          y = text.attr("y"),
          dy = 1,
          tspan = text.text(null).append("tspan").attr("x", 0).attr("y", function(d,i){
            return 0
          }).attr("dy", dy + "em");
        while (word = words.pop()) {
          line.push(word);
          tspan.text(line.join(" "));
          if (tspan.node().getComputedTextLength() > width) {
            line.pop();
            tspan.text(line.join(" "));
            line = [word];
            tspan = text.append("tspan").attr("x", 0).attr("y", function(d,i){
              return 0
            }).attr("dy", ++lineNumber + dy + "em").text(word);
          }
        }
      });
    }
  }

}
