const fs = require('fs');
const path = require('path');
const { PNG } = require('pngjs');
const {
  Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
  HeadingLevel, AlignmentType, WidthType, BorderStyle, ImageRun,
  Header, Footer, PageNumber, NumberFormat, UnderlineType, ShadingType
} = require('docx');

const CAPTURAS_DIR = 'C:\\admin-proyectos\\docs\\Practica3\\capturas';
const OUTPUT_DOCX = 'C:\\admin-proyectos\\docs\\Practica3\\P3_Reporte_Completo_AdminProyectos.docx';

// Ensure dir exists
fs.mkdirSync(path.dirname(OUTPUT_DOCX), { recursive: true });

function p(text, opts = {}) {
  const runs = Array.isArray(text) ? text : [new TextRun({ text, font: 'Arial', size: opts.size || 22, bold: !!opts.bold, italic: !!opts.italic, color: opts.color })];
  return new Paragraph({
    alignment: opts.align || AlignmentType.JUSTIFIED,
    spacing: { line: opts.line || 360, before: opts.before || 120, after: opts.after || 120 },
    children: runs
  });
}

function h1(title) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_1,
    alignment: AlignmentType.LEFT,
    spacing: { line: 360, before: 300, after: 140 },
    children: [
      new TextRun({
        text: title,
        font: 'Arial',
        size: 26, // 13pt
        bold: true,
        color: '003366'
      })
    ]
  });
}

function h2(title) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_2,
    alignment: AlignmentType.LEFT,
    spacing: { line: 360, before: 240, after: 100 },
    children: [
      new TextRun({
        text: title,
        font: 'Arial',
        size: 24, // 12pt
        bold: true,
        color: '1A5276'
      })
    ]
  });
}

function h3(title) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_3,
    alignment: AlignmentType.LEFT,
    spacing: { line: 360, before: 180, after: 80 },
    children: [
      new TextRun({
        text: title,
        font: 'Arial',
        size: 22, // 11pt
        bold: true,
        color: '2E4053'
      })
    ]
  });
}

function cell(text, opts = {}) {
  const pList = Array.isArray(text) ? text : [
    new Paragraph({
      alignment: opts.align || AlignmentType.LEFT,
      spacing: { line: 260, before: 60, after: 60 },
      children: [
        new TextRun({
          text,
          font: 'Arial',
          size: opts.size || 18, // 9pt
          bold: !!opts.bold,
          color: opts.color || (opts.header ? 'FFFFFF' : '000000')
        })
      ]
    })
  ];
  return new TableCell({
    width: opts.width ? { size: opts.width, type: WidthType.DXA } : undefined,
    shading: opts.header ? { type: ShadingType.CLEAR, fill: '003366' } : (opts.bg ? { type: ShadingType.CLEAR, fill: opts.bg } : undefined),
    margins: { top: 100, bottom: 100, left: 140, right: 140 },
    children: pList
  });
}

// 16 cm ≈ 454 pt. docx ImageRun uses points (72 DPI scale).
function imageFigure(imgName, caption, targetWidth = 454) {
  const fileBasename = imgName.endsWith('.png') ? imgName : imgName + '.png';
  const imgPath = path.join(CAPTURAS_DIR, fileBasename);
  const elements = [];
  if (fs.existsSync(imgPath)) {
    try {
      const imgBuffer = fs.readFileSync(imgPath);
      const png = PNG.sync.read(imgBuffer);
      const aspect = png.height / png.width;
      let height = Math.round(targetWidth * aspect);
      let width = targetWidth;
      // Cap height if too tall for standard page presentation
      if (height > 580) {
        height = 580;
        width = Math.round(580 / aspect);
      }
      elements.push(new Paragraph({
        alignment: AlignmentType.CENTER,
        spacing: { before: 180, after: 80 },
        children: [
          new ImageRun({
            data: imgBuffer,
            transformation: { width, height }
          })
        ]
      }));
    } catch (e) {
      console.error('Error loading image', imgName, e);
    }
  } else {
    console.error('Image NOT found:', imgPath);
  }
  elements.push(new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { before: 40, after: 180 },
    children: [
      new TextRun({
        text: caption,
        font: 'Arial',
        size: 18,
        italic: true,
        bold: true,
        color: '555555'
      })
    ]
  }));
  return elements;
}

module.exports = {
  p, h1, h2, h3, cell, imageFigure, OUTPUT_DOCX, CAPTURAS_DIR
};
