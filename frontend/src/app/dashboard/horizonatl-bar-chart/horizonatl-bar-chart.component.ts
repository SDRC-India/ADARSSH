import { Component, OnInit, ElementRef, Input, ViewEncapsulation, ViewChild } from '@angular/core';
import * as d3 from 'd3';
declare var $: any;



@Component({
  selector: 'rmncha-horizonatl-bar-chart',
  templateUrl: './horizonatl-bar-chart.component.html',
  styleUrls: ['./horizonatl-bar-chart.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class HorizonatlBarChartComponent implements OnInit {

  @ViewChild('horizontalBarChart') private chartContainer: ElementRef;
  @Input()
  data: any;
  tooltipOpen = false;

  constructor(private hostRef: ElementRef) {
  }

  ngOnInit() {
    if (this.data) {
      this.createChart(this.data);
    }
  }
  // ngOnChanges(changes){
  //   if(changes.data.currentValue && changes.data.currentValue.length && changes.data.currentValue != changes.data.previousValue){
  //     this.createChart(changes.data.currentValue);
  //   }

  // }


  createChart(data) {
    let col_md_12 = data.align;
    data = data.chartData[0].chartDataValue[0];
    let el = this.chartContainer.nativeElement;
    d3.select(el).select("svg").remove();


    let margin = { top: 25, right: 20, bottom: 100, left: 84 },
      width =
        $(this.hostRef.nativeElement).parent().width() - margin.right - margin.left,
      height = $(this.hostRef.nativeElement).parent().height() - margin.top - margin.bottom;
    let viewportWidth = $(window).width();

    let svg = d3.select(el).append("svg").attr("id",
      "horizontalBar").attr("width",
        width + margin.left + margin.right).attr("height",
          height + margin.top + margin.bottom)
    // var svg = d3.select("#horizontal-bar").attr("width", $(this.hostRef.nativeElement).parent().width()).attr("height",
    //   $(this.hostRef.nativeElement).parent().height()),

    //  margin = {top: 25, right: 20, bottom: 100, left: 80}
    // if($(window).width() > 767){
    //   margin = {top: 0, right: 60, bottom: 100, left: 350}
    // }

    let color = ["#f09943"];
    // var width = +svg.attr("width") - margin.left - margin.right;
    // var height = +svg.attr("height") - margin.top - margin.bottom;
    let x;
    if(col_md_12 == 'col-md-12' && viewportWidth > 991){
      x = d3.scaleLinear().range([0, width - 250]);
    }else{
      x = d3.scaleLinear().range([0, width - 60]);
    }
    let y = d3.scaleBand().domain(d3.range(10, data.length)).range([height, -80]);

    let max = d3.max(data.map(function (d) {
      return parseFloat(d.value);
    }));
    if (max < 100) {
      max = 100
    }

    if (max == undefined) {
      max = 100
    }
    let g = svg.append("g").attr("class", "layer").style("fill",
      function (d, i) {
        return color[i];
      })
      .attr("transform", "translate(" + (max > 10 ? margin.left + 22 : 50) + "," + (margin.left + 6) + ")");
    x.domain([0, max]);
    y.domain(data.map(function (d) { return d.axis; })).padding(0.5);


    g.append("g")
      .attr("class", "x axis")
      .attr("transform", function(){
        if(col_md_12 == 'col-md-12' && viewportWidth > 991){
          return "translate(100," + height + ")"
        }else{
          return "translate(0," + height + ")"
        }
      })
      .call(d3.axisBottom(x).ticks(6))
      .append("text").attr("x",function(){
        if(col_md_12 == 'col-md-12' && viewportWidth > 991){
          return width - 250
        }else{
          return width - 50
        }
      })
      .attr("y", "30").attr("dx", "1em")
      .style("fill", "#333")
      .style("font-weight", "400")
      .attr("font-family", "'Questrial', sans-serif")
      .style("font-size", "13px")
      .text(data[0].unit);

    g.append("g")
      .attr("class", "y axis")
      .attr("transform", function(){
        if(col_md_12 == 'col-md-12' && viewportWidth > 991){
         return "translate(100,0)"
        }else{
          return "translate(0,0)"
        }
      })
      .call(d3.axisLeft(y))
      .selectAll("text")
      .attr("class", "axis")
      .style("fill", "#000")
      .attr("font-family", "'Questrial', sans-serif")
      .call(wrap, (col_md_12 == 'col-md-12' && viewportWidth > 991 )?margin.bottom+80:margin.bottom)
      .on("mouseover", function (d) {
        $(this).popover(
          {
            title: '',
            placement: 'top',
            container: 'body',
            trigger: 'manual',
            html: true,
            animation: true,
            content: function () {
                return "<div style='color: #495769;'>" + "<b>" + d + "</b>" + "</div>";
            }
          });
          $(this).popover('show');
      }
     ).on("mouseout", function (d) {
        removePopovers()
      });

    //check for no data availble
    let allNullValues = true;
    for (let j = 0; j < data.length; j++) {
      if (data[j].value != null) {
        allNullValues = false;
        break;
      }
    }
    if (allNullValues) {
      svg.append("text")
        .attr("transform", "translate(" + ((width / 2) + 100) + ",30)")
        .attr("x", 0)
        .attr("y", 30)
        .attr("font-size", "28px")
        .style("text-anchor", "middle")
        .text("Data Not Available");
      return;
    }
    let bars = g.selectAll(".bar")
      .data(data)
      .enter().append("g").attr("class", "bar");
    bars.append("rect")
      .attr("class", "horizontal")
      .attr("x", function(){
        if(col_md_12 == 'col-md-12' && viewportWidth > 991){
          return 101
        }else{
          return 1
        }
      })
      .attr("height", y.bandwidth())
      .attr("y", function (d) { return y(d.axis); })
      .attr("width", 0)
      .style("cursor", (d) => {
        if (d.value) {
          return 'pointer'
        }
        else {
          return 'default'
        }
      })

      .on("mouseover", function (d) {
        showPopover.call(this, d)
        d3.select(this)
          .attr('fill', "#f08e2e");
      }).on("mouseout", function (d) {
        removePopovers()
        d3.select(this).attr("fill", function () {
          return "#f09943";
        });
      });

    bars.append("text")
      .attr("class", "label")
      //y position of the label is halfway down the bar
      .attr("x", 106)
      .attr("y", function (d) {
        return y(d.axis) + y.bandwidth() / 2 + 4;
      })
      .attr("transform", (d) => {
        if (d.value == null) {
          return "translate(30, 0)"
        }
      })
      .style("fill", (d) => {
        return "#000"
      })
      .style("font-size", "12px")
      .text(0);

    bars.selectAll("rect").transition().duration(2000).attr("width", function (d) { return x(d.value); });

    bars.selectAll("text") .on("mouseover", function (d) {
      showPopover.call(this, d)
    }).on("mouseout", function (d) {
      removePopovers()
    }).transition().duration(2000).delay(0)
      .attr("x", function (d) {
        if(col_md_12 == 'col-md-12' && viewportWidth > 991){
          return x(d.value) + 106;
        }else{
          return x(d.value) + 5;
        }
      })
      .tween("text", function (d) {
        let i = d3.interpolate(0, d.value);
        return function (t) {
          if (d.value > 1) {
            d3.select(this).text(Math.round(i(t)));
          } else {
            d3.select(this).text(i(t));
          }
        };
      });

    function removePopovers() {
      $('.popover').each(function () {
        $(this).remove();
      });
    }
    function showPopover(d) {
      $(this).popover({
        title: '',
        placement: 'top',
        container: 'body',
        trigger: 'manual',
        html: true,
        animation: true,
        content: function () {
          if (d.axis != '' && d.denominator != null && d.numerator != null && d.unit == 'Percentage') {
            return "<div style='color: #495769;'>" + "<b>" + d.axis + "</b>" + "</div>" +
              "<div>" + " Data Value : " + "<span style='color: #495769;font-weight:500;'>" + d.tooltipValue + " (%)" + "</span>" + "</div>" +
              "<div>" + "Numerator : " + "<span style='color: #495769;font-weight:500'>" + d.numerator + "</span>" + "</div>" +
              "<div>" + "Denominator : " + "<span style='color: #495769;font-weight:500'>" + d.denominator + "</span>" + "</div>";
          } else if (d.denominator == null && d.numerator == null && d.unit == 'Percentage') {
            return "<div style='color: #495769;'>" + "<b>" + d.axis + "</b>" + "</div>" +
              "<div>" + " Data Value : " + "<span style='color: #495769;font-weight:500;'>" + d.tooltipValue + " (%)" + "</span>" + "</div>";
          } else if (d.denominator == null && d.numerator != null && d.unit == 'Percentage') {
            return "<div style='color: #495769;'>" + "<b>" + d.axis + "</b>" + "</div>" +
              "<div>" + " Data Value : " + "<span style='color: #495769;font-weight:500;'>" + d.tooltipValue + " (%)" + "</span>" + "</div>" +
              "<div>" + "Numerator : " + "<span style='color: #495769;font-weight:500'>" + d.numerator + "</span>" + "</div>";
          } else if (d.denominator != null && d.numerator == null && d.unit == 'Percentage') {
            return "<div style='color: #495769;'>" + "<b>" + d.axis + "</b>" + "</div>" +
              "<div>" + " Data Value : " + "<span style='color: #495769;font-weight:500;'>" + d.tooltipValue + " (%)" + "</span>" + "</div>" +
              "<div>" + "Denominator : " + "<span style='color: #495769;font-weight:500'>" + d.denominator + "</span>" + "</div>";
          }
          else {
            return "<div style='color: #495769;'>" + "<b>" + d.axis + "</b>" + "</div>" +
              "<div style='color: #495769;'> Data Value: " + d.tooltipValue + "</div>";
          }
        }
      });
      $(this).popover('show');
      // $('.popover.fade.top.in').css('top', parseFloat($('.popover.fade.top.in').css('top').slice(0, -2))+$(window).scrollTop());
    }

    function wrap(text, width) {
      text.each(function () {
        let text = d3.select(this),
          words = text.text().split(/\s+/).reverse(),
          word,
          cnt = 0,
          line = [],
          lineNumber = 0,
          lineHeight = 1,
          y = text.attr("y"),
          dy = parseFloat(text.attr("dy")),
          tspan = text.text(null).append("tspan").attr("x", -8).attr("y", (++lineNumber * lineHeight+y)-2).attr("dy", (dy) + "em").style("font-size", "11.5px");

        while (word = words.pop()) {
          cnt++;
          line.push(word);
          tspan.text(line.join(" "));
          if (tspan.node().getComputedTextLength() > width) {
            line.pop();

            tspan.text(line.join(" "));
            line = [word];
            if (cnt != 1)
              tspan = text.append("tspan").attr("x", -8).attr("y", y - 10).attr("dy", ++lineNumber * lineHeight + dy + "em").style("font-size", "11.5px").text(word);
          }
        }
      });
    }
  }

}
