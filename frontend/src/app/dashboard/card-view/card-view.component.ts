import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import * as d3 from 'd3';
import { Router } from '@angular/router';
declare var $: any;

@Component({
  selector: 'rmncha-card-view',
  templateUrl: './card-view.component.html',
  styleUrls: ['./card-view.component.scss']
})
export class CardViewComponent implements OnInit {
  @Input('data') private data: Array<any>;
  @ViewChild('cardChart') private chartContainer: ElementRef;
  cardDetails: any;
  viewportWidth: number;

  constructor(private hostRef: ElementRef,private router: Router) { }

  ngOnInit() {
    if (this.data) {
      this.cardDetails = this.data;
      this.createSvg(this.data);
    }
    this.viewportWidth = $(window).width();
  }

  createSvg(data) {
    //   Wrap text in a rectangle, and size the text to fit.
    //   d3.textwrap()
    // .container(d3.select("#rectResize"))
    // .resize(true)
    // .draw();
    let el = this.chartContainer.nativeElement;
    let pageName = this.router.url;
    let lastURLSegment = pageName.substr(pageName.lastIndexOf('/') + 1);
    d3.select(el).select("svg").remove();
    let formatComma = d3.format(",")
    let margin = {
      top: 30,
      right: 20,
      bottom: 40,
      left: 20
    },
      width =
        $(this.hostRef.nativeElement).parent().width() - margin.right - margin.left,
      height = $(this.hostRef.nativeElement).parent().height() - margin.top - margin.bottom;

      let x = d3.scaleBand().range([0, width]).padding(0.4);
    // var y = width / 2;

    let svg = d3.select(el).append("svg").attr("id",
      "card")
      .attr("transform","translate(-1,-1)").attr("width",
        width + margin.left + margin.right).
        attr("height",function(){
          if(lastURLSegment != "snapshot-view"){
            return (height - 191);
          }else {
            return (height - 146);
          }
        });

        let rect = svg.append('rect')
     
      .attr("width", width + margin.left + margin.right)
      .attr("height",function(){
        if(lastURLSegment != "snapshot-view"){
          return (height - 191);
        }else {
          return (height - 146);
        }
      })
      .attr("fill", "#f5c022")
      
      .style("cursor", (d) => {
          return 'pointer'
      })
      .on("mouseover", function (d) {
        showPopover.call(this)
        
      }).on("mouseout", function (d) {
        removePopovers()
       
      });
      let text = svg.append("text")
      .attr("transform", "translate(" + (width / 2) + "," + (height - 260)+")")
      .attr("x", 5)
      .attr("y", function(){
        if(lastURLSegment !="snapshot-view"){
          return 15;
        }else{
          return 55;
        }
      })
      .style("text-anchor", "middle")
      .style("fill", "#000")
      .style("font-weight", "500")
      .style("opacity","1")
      .attr("font-family", "'Questrial', sans-serif")
      .style("font-size", "25px")
      .text(function(){
        if(data.indicatorValue == null){
          return 'NA';
        }else if(data.indicatorValue != null && (data.unit == '' || data.unit == 'Number')){
          return formatComma(data.indicatorValue); 
        }else if(data.unit == 'Average'){
          return formatComma(Math.round(data.indicatorValue)) ;
        }else if(data.indicatorValue != null && data.unit == "Percentage"){
          return data.indicatorValue + "%";
        }else{
          return data.indicatorValue ;
        }
      })

      svg.append("text")
      .attr('class','wrapme')
      .attr("transform", "translate(" + ((width / 2) + 10) + "," + (height - 230)+")")
      .attr("x", function(){
        if(data.indicatorName.length < 75){
          return 5;
        }else{
          return 100;
        }
      })
      .attr("y", function(){
        if(lastURLSegment != "snapshot-view"){
          return 15;
        }else{
          return 55;
        }
      })
      .style("text-anchor", "middle")
      .style("fill", "#000")
      .style("font-weight", "400")
      .style("opacity","1")
      .attr("lengthAdjust", "spacingAndGlyphs")
      .attr("font-family", "'Questrial', sans-serif")
      .style("font-size", "12px")
       .text(data.indicatorName)
      .call(wrap, width + margin.left, width);

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
            tspan = text.text(null).append("tspan").attr("x", 0).attr("y", (y - 20)).attr("dy", dy + "em");
            while (word = words.pop()) {
              line.push(word);
              tspan.text(line.join(" "));
              if (tspan.node().getComputedTextLength() > width) {
                line.pop();
                tspan.text(line.join(" "));
                line = [word];
                tspan = text.append("tspan").attr("x", 0).attr("y", (y - 20)).attr("dy", ++lineNumber + dy + "em").text(word);
              }
            }
        });
      }
      function removePopovers() {
        $('.popover').each(function () {
          $(this).remove();
        });
      }
      function showPopover() {
        $(this).popover(
          {
            title: '',
            placement: 'top',
            container: 'html',
            trigger: 'manual',
            html: true,
            animation: true,
            content: function () {
              if( data.denominator != null && data.numerator!=null && data.unit == 'Percentage'){
                return "<div style='color: #495769;'>" +"<b>"+ data.indicatorName +"</b>"+ "</div>" + 
                "<div>" +" Data Value : "+"<span style='color: #495769;font-weight:500;'>"+ data.tooltipValue  +"%"+"</span>"+ "</div>"+
                "<div>" + "Numerator : " +"<span style='color: #495769;font-weight:500'>"+ data.numerator +"</span>"+ "</div>"+
                "<div>" +"Denominator : " +"<span style='color: #495769;font-weight:500'>"+ data.denominator +"</span>"+ "</div>";
              }else if(data.denominator == null && data.numerator==null && data.unit == 'Percentage'){
                return "<div style='color: #495769;'>" +"<b>"+ data.indicatorName +"</b>"+ "</div>" + 
                "<div>" +" Data Value : "+"<span style='color: #495769;font-weight:500;'>"+  (data.tooltipValue == null ? 'NA' : data.tooltipValue  +"%")+"</span>"+ "</div>";
              } else if(data.denominator == null && data.numerator!=null && data.unit == 'Percentage'){
                return "<div style='color: #495769;'>" +"<b>"+ data.indicatorName +"</b>"+ "</div>" + 
                "<div>" +" Data Value : "+"<span style='color: #495769;font-weight:500;'>"+ data.tooltipValue +"%"+"</span>"+ "</div>"+
                "<div>" + "Numerator : " +"<span style='color: #495769;font-weight:500'>"+ data.numerator +"</span>"+ "</div>";
              }else if(data.denominator != null && data.numerator==null && data.unit == 'Percentage'){
                return "<div style='color: #495769;'>" +"<b>"+ data.indicatorName +"</b>"+ "</div>" + 
                "<div>" +" Data Value : "+"<span style='color: #495769;font-weight:500;'>"+ data.tooltipValue +"%"+"</span>"+ "</div>"+
                "<div>" +"Denominator : " +"<span style='color: #495769;font-weight:500'>"+ data.denominator +"</span>"+ "</div>";
              }
            else{
                return "<div style='color: #495769;'>" + "<b>" + data.indicatorName + "</b>" + "</div>" +
                "<div>" +" Data Value : "+"<span style='color: #495769;font-weight:500;'>" + (data.tooltipValue == null ? 'NA' : data.tooltipValue) +"</span>"+ "</div>";
            }
          }
          });
        $(this).popover('show');
      }
  }

}
